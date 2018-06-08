package telran.cars.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.entities.ModelJpa;

public interface ModelRepository extends
JpaRepository<ModelJpa, String> {

	

}
