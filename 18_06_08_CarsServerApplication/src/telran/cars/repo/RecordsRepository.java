package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.cars.entities.RecordJpa;

public interface RecordsRepository extends
JpaRepository<RecordJpa, Integer> {

	

	Stream<RecordJpa> findByReturnDateBefore(LocalDate returnedDateDelete);



	Stream<RecordJpa> findAllBy();

	RecordJpa findByCarRegNumberAndReturnDateNull(String carNumber);

	List<RecordJpa> findByCarFlRemovedTrueAndReturnDateBefore(LocalDate returnedDateDelete);
	@Query(value="select count(*) from carrecords join cars "
			+ "on car_reg_number=reg_number join drivers on driver_license_id="
			+ "license_id where birth_year between :from and :to "
			+ "group by model_model_name order by count(*) desc limit 1",nativeQuery=true)
	Long getMaxCountModels(@Param("from")int from,@Param("to") int to);
	@Query("select car.model.modelName from RecordJpa where"
			+ " driver.birthYear between :from and :to group"
			+ " by car.model.modelName having count(*)=:count")
	List<String> getModelNamesPopular(@Param("from")int from,@Param("to") int to,
			@Param("count")Long count);
	@Query(value="select sum(cost) from carrecords join cars "
			+ "on car_reg_number=reg_number group by model_model_name "
			+ "order by sum(cost) desc limit 1",nativeQuery=true)
	Double getMaxCost();
	@Query("select car.model.modelName from RecordJpa "
			+ "group by car.model.modelName having sum(cost)=:profit")
	List<String> getModelNamesMostProfitable(@Param("profit")
	double profit);

}
