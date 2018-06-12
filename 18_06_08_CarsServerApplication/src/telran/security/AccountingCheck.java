package telran.security;

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
	@SuppressWarnings("deprecation")
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
	return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
	@Autowired
	IAccounts accounts;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String password=accounts.getPassword(username);
		if(password==null)
			throw new UsernameNotFoundException("");
		return new User(username, password,
				AuthorityUtils.createAuthorityList("ROLE_USER"));
	}

}
