package telran.cars.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.cars.model.*;
import telran.cars.dto.*;
@SpringBootApplication
@RestController
@ComponentScan({"telran.cars.model","telran.security"})
@EnableJpaRepositories("telran.cars.repo")
@EntityScan("telran.cars.entities")
public class CarsRestAppl {
	@Autowired
IRentCompany company;
@PostMapping(value=CarsApiConstants.ADD_CAR_MODEL)
CarsReturnCode addCarModel(@RequestBody Model carModel) {
	return company.addModel(carModel);
}
@RequestMapping(value=CarsApiConstants.GET_MODEL)
Model getModel(String modelName) {
	return company.getModel(modelName);
}
@PostMapping(value=CarsApiConstants.ADD_CAR)
CarsReturnCode addCar(@RequestBody Car car) {
	return company.addCar(car);
}
@PostMapping(value=CarsApiConstants.ADD_DRIVER)
CarsReturnCode addDriver(@RequestBody Driver driver) {
	return company.addDriver(driver);
}
@RequestMapping(value=CarsApiConstants.GET_CAR)
Car getCar(String carNumber) {
	return company.getCar(carNumber);
}
@RequestMapping(value=CarsApiConstants.GET_DRIVER)
Driver getCar( long licenseId) {
	return company.getDriver(licenseId);
}

@PostMapping (value=CarsApiConstants.CLEAR_CARS)
List<Car> clearCars(@RequestBody DateDays dd){
	return company.clear(dd.date, dd.days);
}
@RequestMapping(value=CarsApiConstants.GET_ALL_CARS)
List<Car> getAllCars(){
	return company.getAllCars().collect(Collectors.toList());
}
@RequestMapping(value=CarsApiConstants.GET_ALL_DRIVERS)
List<Driver> getAllDrivers(){
	return company.getAllDrivers().collect(Collectors.toList());
}
@RequestMapping(value=CarsApiConstants.GET_ALL_RECORDS)
List<RentRecord> getAllRecords(){
	return company.getAllRecords().collect(Collectors.toList());
}
@RequestMapping(value=CarsApiConstants.GET_ALL_MODELS)
List<String> getAllModels(){
	return company.getAllModels();
}
@PostMapping(value=CarsApiConstants.REMOVE_CAR)
CarsReturnCode removeCar(@RequestBody String carNumber) {
	return company.removeCar(carNumber);
}
@PostMapping(value=CarsApiConstants.RENT_CAR)
CarsReturnCode rentCar(@RequestBody RentRecord record) {
	return company.rentCar(record.getCarNumber(),
			record.getLicenseId(), record.getRentDate(),
			record.getRentDays());
}
@PostMapping(value=CarsApiConstants.RETURN_CAR)
CarsReturnCode returnCar(@RequestBody RentRecord record) {
	return company.returnCar(record.getCarNumber(),record.getLicenseId(),record.getReturnDate()
			,record.getGasTankPercent(),record.getDamages());
}
@RequestMapping(value=CarsApiConstants.GET_CAR_DRIVERS)
List<Driver> getCarDrivers(String carNumber) {
	return company.getCarDrivers(carNumber);
}
@RequestMapping(value=CarsApiConstants.GET_DRIVER_CARS)
List<Car> getDriverCars(long licenseId) {
	return company.getDriverCars(licenseId);
}
@PostMapping(value=CarsApiConstants.SAVE)
String save() {
	company.save();
	return "should be saved";
}
@GetMapping(value=CarsApiConstants.MOST_POPULAR_CARS)
Iterable<String> getMostPopularCarModels(@RequestParam
		(CarsApiConstants.YEAR_FROM) int from, @RequestParam
		(CarsApiConstants.YEAR_TO) int to){
	return company.getMostPopularModels(from, to);
}
@GetMapping(value=CarsApiConstants.MOST_PROFIT_CARS)
Iterable<String> getMostProfitCarModels(){
	return company.getMostProfitableModels();
}

	public static void main(String[] args) {
		SpringApplication.run(CarsRestAppl.class , args);

	}

}
