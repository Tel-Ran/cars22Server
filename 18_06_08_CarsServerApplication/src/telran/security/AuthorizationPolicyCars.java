package telran.security;
import static telran.cars.dto.CarsApiConstants.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
@Configuration
public class AuthorizationPolicyCars extends
WebSecurityConfigurerAdapter {
@Override
protected void configure(HttpSecurity http)throws Exception{
	http.csrf().disable();//allows for spring security 2 running POST requests
	http.httpBasic();//enabling web security
	//only ADMIN may perform the following requests
	http.authorizeRequests().antMatchers(ADD_CAR,ADD_CAR_MODEL,REMOVE_CAR,CLEAR_CARS)
	.hasRole("ADMIN");
	//only CLERK may perform the following requests
	http.authorizeRequests().antMatchers(RENT_CAR,RETURN_CAR,ADD_DRIVER).hasRole("CLERK");
	//only DRIVER may perform the following requests
	http.authorizeRequests().antMatchers(GET_CAR_DRIVERS,GET_DRIVER_CARS).hasRole("DRIVER");
	//only STATIST may perform the following requests
	http.authorizeRequests().antMatchers(MOST_POPULAR_CARS,MOST_PROFIT_CARS).hasRole("STATIST");
	//only TECHNICIAN may perform the following requests
	http.authorizeRequests().antMatchers(GET_ALL_RECORDS,GET_ALL_DRIVERS).hasRole("TECHNICIAN");
	//only ADMIN and DRIVER may perform the following methods
	http.authorizeRequests().antMatchers(GET_CAR).hasAnyRole("ADMIN","DRIVER");
	//only CLERK and DRIVER may perform the following methods
	http.authorizeRequests().antMatchers(GET_DRIVER).hasAnyRole("CLERK","DRIVER");
	//any role may perform the following methods
	http.authorizeRequests().antMatchers(GET_ALL_CARS).authenticated();
	//any non-authorized user may perform the following methods
	http.authorizeRequests().antMatchers(GET_ALL_MODELS,GET_MODEL).permitAll();
}
}
