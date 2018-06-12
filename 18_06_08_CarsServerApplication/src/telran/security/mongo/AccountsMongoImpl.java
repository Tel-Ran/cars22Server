package telran.security.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.security.IAccounts;
import telran.security.mongo.repo.AccountRepository;
@Service
public class AccountsMongoImpl implements IAccounts {
@Autowired
AccountRepository accounts;
	@Override
	public String getPassword(String username) {
		AccountMongo account=accounts.findById(username).orElse(null);
		String password=null;
		if(account!=null)
			password=account.getPassword();
		return password;
	}

	@Override
	public String[] getRoles(String username) {
		AccountMongo account=accounts.findById(username).orElse(null);
		String roles[]=null;
		if(account!=null)
			roles=account.getRoles();
		return roles;
	}

}
