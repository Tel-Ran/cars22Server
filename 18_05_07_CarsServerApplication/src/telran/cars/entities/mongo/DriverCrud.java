package telran.cars.entities.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.cars.dto.*;
@Document(collection="drivers")
public class DriverCrud {
private int birthYear;
@Id
private long licenseId;
private String name;
private String phone;
public DriverCrud() {}
public DriverCrud(Driver driver) {
	birthYear=driver.getBirthYear();
	licenseId=driver.getLicenseId();
	name=driver.getName();
	phone=driver.getPhone();
}
public Driver getDriver() {
	Driver driver=
			new Driver(licenseId, name, birthYear,
					phone);
	return driver;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public int getBirthYear() {
	return birthYear;
}
public long getLicenseId() {
	return licenseId;
}
public String getName() {
	return name;
}

}
