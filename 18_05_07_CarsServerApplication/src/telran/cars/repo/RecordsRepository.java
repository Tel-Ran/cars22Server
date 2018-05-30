package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.cars.entities.RecordJpa;

public interface RecordsRepository extends
JpaRepository<RecordJpa, Integer> {

	RecordJpa findByCarNumberAndReturnDateNull(String carNumber);

	Stream<RecordJpa> findByReturnDateBefore(LocalDate returnedDateDelete);

	Stream<RecordJpa> findByCarNumber(String carNumber);

	Stream<RecordJpa> findByLicenseId(long licenseId);

	Stream<RecordJpa> findAllBy();

}
