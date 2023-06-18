package de.teamg.antique.data.repository;

import de.teamg.antique.data.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "SELECT c FROM Car c WHERE concat(lower(c.licensePlate), lower(c.designation), c.modelYears, c.hp, c.cc, lower(c.fuel), lower(c.insuranceNumber), c.tuv, c.pricePerDay, c.pricePerKm) LIKE lower(concat('%', :filter, '%'))")
    List<Car> findAllFilterAllColumns(String filter);

}