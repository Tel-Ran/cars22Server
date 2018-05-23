package telran.cars.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.cars.dto.Car;
import telran.cars.dto.RentRecord;
import telran.cars.entities.mongo.RentRecordCrud;

public interface RecordsRepository extends
MongoRepository<RentRecordCrud, Long> {

	RentRecordCrud findByCarNumberAndReturnDateNull(String carNumber);

	Stream<RentRecordCrud> findByReturnDateBefore(LocalDate returnedDateDelete);

	Stream<RentRecordCrud> findByCarNumber(String carNumber);

	Stream<RentRecordCrud> findByLicenseId(long licenseId);

	Stream<RentRecordCrud> findAllBy();

}
