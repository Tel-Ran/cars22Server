package telran.cars.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.cars.dto.*;
@Document(collection="cars")
public class CarCrud {
private String color;
private String modelName;
@Id
private String regNumber;
private State state;
private boolean flRemoved;
private boolean inUse;
public CarCrud() {}
public CarCrud(Car car) {
	color=car.getColor();
	modelName=car.getModelName();
	regNumber=car.getRegNumber();
	state=car.getState();
	flRemoved=car.isFlRemoved();
	inUse=car.isInUse();
}

public State getState() {
	return state;
}

public void setState(State state) {
	this.state = state;
}

public boolean isFlRemoved() {
	return flRemoved;
}

public void setFlRemoved(boolean flRemoved) {
	this.flRemoved = flRemoved;
}

public boolean isInUse() {
	return inUse;
}

public void setInUse(boolean inUse) {
	this.inUse = inUse;
}

public String getColor() {
	return color;
}

public String getModelName() {
	return modelName;
}

public String getRegNumber() {
	return regNumber;
}
public Car getCar() {
	Car car=new Car(regNumber, color, modelName);
	car.setFlRemoved(flRemoved);
	car.setInUse(inUse);
	car.setState(state);
	return car;
}
}
