package telran.security;

public interface IAccounts {
String getPassword(String username);
String[] getRoles(String username);
}
