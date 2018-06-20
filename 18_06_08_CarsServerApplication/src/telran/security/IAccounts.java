package telran.security;

import java.time.LocalDate;

public interface IAccounts {
String getPassword(String username);
String[] getRoles(String username);
LocalDate getExpirationDate(String username);
}
