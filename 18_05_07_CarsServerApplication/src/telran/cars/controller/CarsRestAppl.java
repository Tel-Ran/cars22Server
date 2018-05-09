package telran.cars.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.cars.model.IRentCompany;
import telran.cars.model.RentCompanyEmbedded;
import telran.cars.dto.*;
@SpringBootApplication
@RestController
public class CarsRestAppl {
IRentCompany company=RentCompanyEmbedded.restoreFromFile
("cars.data");
@PostMapping(value=CarsApiConstants.ADD_CAR_MODEL)
CarsReturnCode addCarModel(@RequestBody Model carModel) {
	return company.addModel(carModel);
}
@RequestMapping(value=CarsApiConstants.GET_MODEL)
Model getModel(String modelName) {
	return company.getModel(modelName);
}
	public static void main(String[] args) {
		SpringApplication.run(CarsRestAppl.class , args);

	}

}
