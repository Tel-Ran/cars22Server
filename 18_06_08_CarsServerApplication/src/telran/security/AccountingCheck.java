package telran.security;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
@Configuration
public class AccountingCheck implements UserDetailsService{
	
	@Autowired
	IAccounts accounts;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password=accounts.getPassword(username);
		if(password==null||
				accounts.getExpirationDate(username).isBefore(LocalDate.now()))
			throw new UsernameNotFoundException("");
		return new User(username, "{bcrypt}"+password,
				AuthorityUtils.createAuthorityList(accounts.getRoles(username)));
	}

}
