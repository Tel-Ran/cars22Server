package telran.cars.model;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.cars.dto.*;
import telran.cars.entities.mongo.*;
import telran.cars.repo.CarRepository;
import telran.cars.repo.DriverRepository;
import telran.cars.repo.ModelRepository;
import telran.cars.repo.RecordsRepository;
@Service
public class RentCompanyMongo extends AbstractRentCompany {
@Autowired
ModelRepository models;
@Autowired
CarRepository cars;
@Autowired
DriverRepository drivers;
@Autowired
RecordsRepository records;
	@Override
	public CarsReturnCode addModel(Model model) {
		if(models.existsById(model.getModelName()))
			return CarsReturnCode.MODEL_EXISTS;
		models.save(new ModelCrud(model));
		return CarsReturnCode.OK;
	}

	@Override
	public CarsReturnCode addCar(Car car) {
		if(!models.existsById(car.getModelName()))
			return CarsReturnCode.NO_MODEL;
		if(cars.existsById(car.getRegNumber()))
			return CarsReturnCode.CAR_EXISTS;
		cars.save(new CarCrud(car));
		return CarsReturnCode.OK;
	}

	@Override
	public CarsReturnCode addDriver(Driver driver) {
		if(drivers.existsById(driver.getLicenseId()))
			return CarsReturnCode.DRIVER_EXISTS;
		drivers.save(new DriverCrud(driver));
		return CarsReturnCode.OK;
	}

	@Override
	public Model getModel(String modelName) {
		
		return models.findById(modelName).get().getModel();
	}

	@Override
	public Car getCar(String carNumber) {
		return cars.findById(carNumber).get().getCar();
	}

	@Override
	public Driver getDriver(long licenseId) {
		return drivers.findById(licenseId).get().getDriver();
	}

	@Override
	public CarsReturnCode rentCar
	(String carNumber, long licenseId, LocalDate rentDate, int rentDays) {
		CarCrud car=cars.findById(carNumber).get();
		if(car==null||car.isFlRemoved())
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		if(getDriver(licenseId)==null)
			return CarsReturnCode.NO_DRIVER;
		RentRecord record=new RentRecord(licenseId, carNumber, rentDate, rentDays);
		records.save(new RentRecordCrud(record));
		setInUse(true,car);
		return CarsReturnCode.OK;
	}
	private void setInUse(boolean inUse, CarCrud car) {
		car.setInUse(inUse);
		cars.save(car);
		
	}
	@Override
	public CarsReturnCode returnCar(String carNumber, long licenseId, LocalDate returnDate, int gasTankPercent,
			int damages) {
		if(getDriver(licenseId)==null)
			return CarsReturnCode.NO_DRIVER;
		RentRecordCrud recordCrud=records.findByCarNumberAndReturnDateNull(carNumber);
		if(recordCrud==null)
			return CarsReturnCode.CAR_NOT_RENTED;
		CarCrud car=cars.findById(carNumber).get();
		if(car==null||car.isFlRemoved())
			return CarsReturnCode.NO_CAR;
		
		if(returnDate.isBefore(recordCrud.getRentDate()))
			return CarsReturnCode.RETURN_DATE_WRONG;
		updateRecordCrud(returnDate, gasTankPercent, damages, recordCrud,car);
		updateCarData(damages, car);
		return CarsReturnCode.OK;
	}

	private void updateRecordCrud(LocalDate returnDate, int gasTankPercent, int damages, RentRecordCrud recordCrud,CarCrud car) {
		recordCrud.setDamages(damages);
		recordCrud.setGasTankPercent(gasTankPercent);
		recordCrud.setReturnDate(returnDate);
		setCost(recordCrud,car);
		records.save(recordCrud);
	}

	
	private void updateCarData(int damages, CarCrud car) {
		if(damages>0 && damages<10)
			car.setState(State.GOOD);
		else if(damages>=10&&damages<30)
			car.setState(State.BAD);
		else if(damages>=30)
			car.setFlRemoved(true);
		car.setInUse(false);
		cars.save(car);
	}

	private void setCost(RentRecordCrud record, CarCrud car) {
		long period=ChronoUnit.DAYS.between
				(record.getRentDate(), record.getReturnDate());
		float costPeriod=0;
		Model model=getModel(car.getModelName());
		float costGas=0;
		costPeriod = getCostPeriod(record, period, model);
		costGas = getCostGas(record, model);
		record.setCost(costPeriod+costGas);
		
	}
	private float getCostGas(RentRecordCrud record, Model model) {
		float costGas;
		int gasTank=model.getGasTank();
		float litersCost=(float)(100-record.getGasTankPercent())*gasTank/100;
		costGas=litersCost*companyData.getGasPrice();
		return costGas;
	}
	private float getCostPeriod(RentRecordCrud record, long period, Model model) {
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
	public CarsReturnCode removeCar(String carNumber) {
		CarCrud car=cars.findById(carNumber).get();
		if(car==null)
			return CarsReturnCode.NO_CAR;
		if(car.isInUse())
			return CarsReturnCode.CAR_IN_USE;
		car.setFlRemoved(true);
		cars.save(car);
		return CarsReturnCode.OK;
	}

	@Override
	public List<Car> clear(LocalDate currentDate, int days) {
		LocalDate returnedDateDelete=currentDate.minusDays(days);
		List<RentRecordCrud> recordsForDelete=getRecordsForDelete(returnedDateDelete);
		List<CarCrud> carsForDelete=getCarsForDelete(recordsForDelete);
		deleteRecords(recordsForDelete);
		deleteCars(carsForDelete);
		return toCars(carsForDelete);
	}
	
	private List<Car> toCars(List<CarCrud> carsCrud) {
		return carsCrud.stream()
				.map(CarCrud::getCar)
				.collect(Collectors.toList());
	}

	private void deleteCars(List<CarCrud> carsForDelete) {
		carsForDelete.forEach(c->cars.delete(c));
		
	}

	private void deleteRecords(List<RentRecordCrud> recordsForDelete) {
		recordsForDelete.forEach(r->records.delete(r));
		
	}

	private List<CarCrud> getCarsForDelete(List<RentRecordCrud> recordsForDelete) {
		
		return recordsForDelete.stream().map(r->cars.findById(r.getCarNumber()).get())
				.distinct().collect(Collectors.toList());
	}
	private List<RentRecordCrud> getRecordsForDelete(LocalDate returnedDateDelete) {
		return records.findByReturnDateBefore(returnedDateDelete).filter
		(r->getCar(r.getCarNumber()).isFlRemoved()).collect(Collectors.toList());
		
	}

	
	
	

	@Override
	public List<Driver> getCarDrivers(String carNumber) {
		List<Driver> drivers=records.findByCarNumber(carNumber)
				.map(r->getDriver(r.getLicenseId())).collect(Collectors.toList());
		return drivers;
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
