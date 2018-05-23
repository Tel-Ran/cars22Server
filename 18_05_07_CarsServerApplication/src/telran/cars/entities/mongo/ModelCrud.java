package telran.cars.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.cars.dto.*;
@Document(collection="models")
public class ModelCrud {
private String company;
private String country;
private int gasTank;
@Id
private String modelName;
private int priceDay;
public ModelCrud() {}
public ModelCrud(Model model) {
	company=model.getCompany();
	country=model.getCountry();
	gasTank=model.getGasTank();
	modelName=model.getModelName();
	priceDay=model.getPriceDay();
	
}
public Model getModel() {
	Model model=new Model
			(modelName, gasTank, company,
					country, priceDay);
	return model;
}
public int getPriceDay() {
	return priceDay;
}
public void setPriceDay(int priceDay) {
	this.priceDay = priceDay;
}
public String getCompany() {
	return company;
}
public String getCountry() {
	return country;
}
public int getGasTank() {
	return gasTank;
}
public String getModelName() {
	return modelName;
}

}
