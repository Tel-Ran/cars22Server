package telran.cars.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import telran.cars.dto.*;
import telran.cars.entities.CarJpa;
import telran.cars.entities.DriverJpa;
import telran.cars.entities.ModelJpa;
import telran.cars.entities.RecordJpa;
import telran.cars.repo.CarRepository;
import telran.cars.repo.DriverRepository;
import telran.cars.repo.ModelRepository;
import telran.cars.repo.RecordsRepository;
@Service
public class RentCompanyJpa extends AbstractRentCompany {
@Autowired
ModelRepository models;
@Autowired
CarRepository cars;
@Autowired
DriverRepository drivers;
@Autowired
RecordsRepository records;
	@Override
	@Transactional
	public CarsReturnCode addModel(Model model) {
		if(models.existsById(model.getModelName()))
			return CarsReturnCode.MODEL_EXISTS;
		models.save(new ModelJpa
				(model.getModelName(),
				model.getGasTank(),
				model.getCompany(),
				model.getCountry(), model.getPriceDay()));
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public CarsReturnCode addCar(Car car) {
		if(!models.existsById(car.getModelName()))
			return CarsReturnCode.NO_MODEL;
		if(cars.existsById(car.getRegNumber()))
			return CarsReturnCode.CAR_EXISTS;
		ModelJpa model=models.findById
				(car.getModelName()).get();
		cars.save(new CarJpa
		(car.getRegNumber(), car.getColor(),
		car.getState(), false, false, model));
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public CarsReturnCode addDriver(Driver driver) {
		if(drivers.existsById(driver.getLicenseId()))
			return CarsReturnCode.DRIVER_EXISTS;
		drivers.save(new DriverJpa
				(driver.getLicenseId(), driver.getName(),
				driver.getBirthYear(),
				driver.getPhone()));
		return CarsReturnCode.OK;
	}

	@Override
	public Model getModel(String modelName) {
		ModelJpa modelJpa=models.findById(modelName).get();
		return modelJpa==null?null:getModel(modelJpa);
	}

	private Model getModel(ModelJpa modelJpa) {
		Model res=new Model
				(modelJpa.getModelName(),modelJpa.getGasTank(),
						modelJpa.getCompany(), modelJpa.getCountry(),
						modelJpa.getPriceDay());
		return res;
	}

	@Override
	public Car getCar(String carNumber) {
		CarJpa carJpa=cars.findById(carNumber).get();
		return carJpa==null?null:getCarDto(carJpa);
	}

	private Car getCarDto(CarJpa carJpa) {
		return new Car(carJpa.getRegNumber(), carJpa.getColor(),
				carJpa.getModel().getModelName());
	}

	@Override
	public Driver getDriver(long licenseId) {
		DriverJpa driverJpa = drivers.findById(licenseId).get();
		return driverJpa==null?null:getDriverDto(driverJpa);
	}

	private Driver getDriverDto(DriverJpa driverJpa) {
		return new Driver(driverJpa.getLicenseId(),
				driverJpa.getName(), driverJpa.getBirthYear(), driverJpa.getPhone());
	}

	@Override
	@Transactional
	public CarsReturnCode rentCar
	(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
		CarJpa car=cars.findById(carNumber).get();
		if(car==null||car.isFlRemoved())
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		DriverJpa driver=drivers.findById(licenseId).get();
		if(driver==null)
			return CarsReturnCode.NO_DRIVER;
		RecordJpa recordJpa=new RecordJpa(driver, car, rentDate, rentDays);
		records.save(recordJpa);
		car.setInUse(true);
		return CarsReturnCode.OK;
	}
	
	@Override
	@Transactional
	public CarsReturnCode returnCar(String carNumber, long licenseId, LocalDate returnDate,
			int gasTankPercent,
			int damages) {
		RecordJpa recordJpa=records.findByCarRegNumberAndReturnDateNull(carNumber);
		if(recordJpa==null)
			return CarsReturnCode.CAR_NOT_RENTED;
		if(returnDate.isBefore(recordJpa.getRentDate()))
			return CarsReturnCode.RETURN_DATE_WRONG;
		CarJpa car=cars.findById(carNumber).get();
		updateRecordCrud(returnDate, gasTankPercent, damages, recordJpa,car);
		updateCarData(damages, car);
		return CarsReturnCode.OK;
	}

	private void updateRecordCrud(LocalDate returnDate, int gasTankPercent, int damages,
			RecordJpa recordJpa,CarJpa car) {
		recordJpa.setDamages(damages);
		recordJpa.setGasTankPercent(gasTankPercent);
		recordJpa.setReturnDate(returnDate);
		setCost(recordJpa,car);
		
	}

	
	private void updateCarData(int damages, CarJpa car) {
		if(damages>0 && damages<10)
			car.setState(State.GOOD);
		else if(damages>=10&&damages<30)
			car.setState(State.BAD);
		else if(damages>=30)
			car.setFlRemoved(true);
		car.setInUse(false);
		
	}

	private void setCost(RecordJpa record, CarJpa car) {
		long period=ChronoUnit.DAYS.between
				(record.getRentDate(), record.getReturnDate());
		float costPeriod=0;
		ModelJpa model=car.getModel();
		float costGas=0;
		costPeriod = getCostPeriod(record, period, model);
		costGas = getCostGas(record, model);
		record.setCost(costPeriod+costGas);
		
	}
	private float getCostGas(RecordJpa record, ModelJpa model) {
		float costGas;
		int gasTank=model.getGasTank();
		float litersCost=(float)(100-record.getGasTankPercent())*gasTank/100;
		costGas=litersCost*companyData.getGasPrice();
		return costGas;
	}
	private float getCostPeriod(RecordJpa record, long period, ModelJpa model) {
		float costPeriod;
		long delta=period-record.getRentDays();
		float additionalPeriodCost=0;
		
		if(model==null)
			throw new IllegalArgumentException("Car contains wrong model");
		int pricePerDay=model.getPriceDay();
		int rentDays=record.getRentDays();
		if(delta>0){
			additionalPeriodCost=getAdditionalPeriodCost
					(pricePerDay,delta);
		}
		costPeriod=rentDays*pricePerDay+additionalPeriodCost;
		return costPeriod;
	}

	private float getAdditionalPeriodCost(int pricePerDay, long delta) {
		float fineCostPerDay=pricePerDay*companyData.getFinePercent()/100;
		return (pricePerDay+fineCostPerDay)*delta;
	}

	@Override
	@Transactional
	public CarsReturnCode removeCar(String carNumber) {
		CarJpa car=cars.findById(carNumber).get();
		if(car==null)
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		car.setFlRemoved(true);
		return CarsReturnCode.OK;
	}

	@Override
	@Transactional
	public List<Car> clear(LocalDate currentDate, int days) {
		LocalDate returnedDateDelete=currentDate.minusDays(days);
		List<RecordJpa> recordsForDelete=getRecordsForDelete(returnedDateDelete);
		List<CarJpa> carsForDelete=getCarsJpaForDelete(recordsForDelete);
		List<Car> res=carsForDelete.stream().map(this::getCarDto).collect(Collectors.toList());
		carsForDelete.forEach(cars::delete);
		return res;
	}
	
	private List<CarJpa> getCarsJpaForDelete(List<RecordJpa> recordsForDelete) {
		
		return recordsForDelete.stream().map(RecordJpa::getCar)
				.collect(Collectors.toList());
	}
	private List<RecordJpa> getRecordsForDelete(LocalDate returnedDateDelete) {
		return records.findByCarFlRemovedTrueAndReturnDateBefore(returnedDateDelete);
		
	}

	
	
	

	@Override
	public List<Driver> getCarDrivers(String carNumber) {
		CarJpa car=cars.findById(carNumber).get();
		if(car==null)
			return new ArrayList<>();
		List<Driver> res=car.getRecords().stream()
				.map(r->getDriverDto(r.getDriver())).distinct()
				.collect(Collectors.toList());
		return res;
	}

	@Override
	public List<Car> getDriverCars(long licenseId) {
		List<Car> res=records.findByLicenseId(licenseId)
				.map(r->getCar(r.getCarNumber())).collect(Collectors.toList());
		return res;
	}

	@Override
	public Stream<Car> getAllCars() {
		
		return cars.findAllBy().map(CarCrud::getCar);
	}

	@Override
	public Stream<Driver> getAllDrivers() {
		return drivers.findAllBy().map(DriverCrud::getDriver);
	}

	@Override
	public Stream<RentRecord> getAllRecords() {
		return records.findAllBy().map(RentRecordCrud::getRentRecord);
	}

	@Override
	public List<String> getAllModels() {
		
		return models.findAll().stream()
				.map(ModelCrud::getModelName)
				.collect(Collectors.toList());
	}

	@Override
	public void save() {
		
		
	}

}
