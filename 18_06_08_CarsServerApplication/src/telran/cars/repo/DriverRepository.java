package telran.cars.repo;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.entities.DriverJpa;

public interface DriverRepository extends
JpaRepository<DriverJpa, Long> {

	Stream<DriverJpa> findAllBy();

}
