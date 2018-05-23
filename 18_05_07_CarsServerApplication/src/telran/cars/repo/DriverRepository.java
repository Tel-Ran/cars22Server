package telran.cars.repo;

import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.cars.dto.Driver;
import telran.cars.entities.mongo.DriverCrud;

public interface DriverRepository extends
MongoRepository<DriverCrud, Long> {

	Stream<DriverCrud> findAllBy();

}
