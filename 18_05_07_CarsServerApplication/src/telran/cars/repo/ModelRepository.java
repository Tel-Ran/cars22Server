package telran.cars.repo;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.cars.entities.mongo.ModelCrud;

public interface ModelRepository extends
MongoRepository<ModelCrud, String> {

	

}
