package telran.security.mongo.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.security.mongo.AccountMongo;

public interface AccountRepository extends
MongoRepository<AccountMongo, String>{

}
