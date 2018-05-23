package telran.cars.entities.mongo;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.cars.dto.*;
@Document(collection="rent_records")
public class RentRecordCrud {
	@Id
private long id;
private String carNumber;
private float cost;
private int damages;
private int gasTankPercent;
private long licenseId;
private LocalDate rentDate;
private LocalDate returnDate;
private int rentDays;
public RentRecordCrud() {}
public RentRecordCrud(RentRecord record) {
	id=System.nanoTime();
	carNumber=record.getCarNumber();
	cost=record.getCost();
	damages=record.getDamages();
	gasTankPercent=record.getGasTankPercent();
	licenseId=record.getLicenseId();
	rentDate=record.getRentDate();
	returnDate=record.getReturnDate();
	rentDays=record.getRentDays();
}
public RentRecord getRentRecord() {
	RentRecord record=new RentRecord
			(licenseId, carNumber, rentDate,
					rentDays);
	record.setCost(cost);
	record.setDamages(damages);
	record.setGasTankPercent(gasTankPercent);
	record.setReturnDate(returnDate);
	return record;
}
public float getCost() {
	return cost;
}
public void setCost(float cost) {
	this.cost = cost;
}
public int getDamages() {
	return damages;
}
public void setDamages(int damages) {
	this.damages = damages;
}
public int getGasTankPercent() {
	return gasTankPercent;
}
public void setGasTankPercent(int gasTankPercent) {
	this.gasTankPercent = gasTankPercent;
}
public LocalDate getReturnDate() {
	return returnDate;
}
public void setReturnDate(LocalDate returnDate) {
	this.returnDate = returnDate;
}
public int getRentDays() {
	return rentDays;
}
public void setRentDays(int rentDays) {
	this.rentDays = rentDays;
}
public long getId() {
	return id;
}
public String getCarNumber() {
	return carNumber;
}
public long getLicenseId() {
	return licenseId;
}
public LocalDate getRentDate() {
	return rentDate;
}

}
