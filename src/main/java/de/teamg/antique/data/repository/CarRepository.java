package de.teamg.antique.data.repository;

import de.teamg.antique.data.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "SELECT * FROM CAR WHERE (lower(license_plate), lower(designation), lower(model_years), lower(hp), lower(cc), lower(fuel), lower(insurance_number), lower(tuv), lower(price_per_day), lower(price_per_km)) LIKE lower(concat('%', :filter, '%'))", nativeQuery = true)
    List<Car> findAllFilterAllColumns(String filter);

}