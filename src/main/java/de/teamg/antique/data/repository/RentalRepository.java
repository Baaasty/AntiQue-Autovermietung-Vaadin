package de.teamg.antique.data.repository;

import de.teamg.antique.data.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query(value = "SELECT r FROM Rental r WHERE concat(r.rentalStart, r.rentalEnd, r.kmStart, r.kmEnd, r.pricePerDay, r.pricePerKm) LIKE lower(concat('%', :filter, '%'))")
    List<Rental> findAllFilterAllColumns(String filter);

}