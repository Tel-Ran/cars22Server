package telran.security.mongo;

import java.time.LocalDate;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="accounts")
public class AccountMongo {
@Id
String username;
String password;
String roles[];
LocalDate expirationDate;
public LocalDate getExpirationDate() {
	return expirationDate;
}
public void setExpirationDate(LocalDate expirationDate) {
	this.expirationDate = expirationDate;
}
public AccountMongo(String username, String password, String[] roles) {
	super();
	this.username = username;
	this.password = password;
	this.roles = roles;
}
public AccountMongo() {}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String[] getRoles() {
	return roles;
}
public void setRoles(String[] roles) {
	this.roles = roles;
}
public String getUsername() {
	return username;
}

}
