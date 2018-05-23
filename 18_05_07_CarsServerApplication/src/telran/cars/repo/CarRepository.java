package telran.cars.repo;

import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.cars.dto.Car;
import telran.cars.entities.mongo.CarCrud;

public interface CarRepository extends 
MongoRepository<CarCrud, String> {

	Stream<CarCrud> findAllBy();

}
