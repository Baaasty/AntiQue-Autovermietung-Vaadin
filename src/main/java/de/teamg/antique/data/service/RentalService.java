package de.teamg.antique.data.service;

import de.teamg.antique.data.dto.RentalDTO;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.exception.RentalNotFoundException;
import de.teamg.antique.data.repository.RentalRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"rentals"})
public class RentalService {

    private final RentalRepository rentalRepository;

    private final CarService carService;

    private final PersonService personService;

    public RentalService(RentalRepository rentalRepository, CarService carService, PersonService personService) {
        this.rentalRepository = rentalRepository;
        this.carService = carService;
        this.personService = personService;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getAllRentalsFiltered(String filter) {
        return rentalRepository.findAllFilterAllColumns(filter);
    }

    @Cacheable(key = "#id")
    public Rental getRentalById(long id) {
        return rentalRepository.findById(id).orElseThrow(() -> new RentalNotFoundException(id));
    }

    @CachePut(key = "#result.id")
    public Rental createRental(RentalDTO rentalDTO) {
        return createRental(new Rental(
                rentalDTO.rentalStart(),
                rentalDTO.rentalEnd(),
                rentalDTO.kmStart(),
                rentalDTO.kmEnd(),
                rentalDTO.pricePerDay(),
                rentalDTO.pricePerKm(),
                carService.getCarById(rentalDTO.carId()),
                personService.getPersonById(rentalDTO.customerPersonId()),
                personService.getPersonById(rentalDTO.employeePersonId())
        ));
    }

    @CachePut(key = "#result.id")
    public Rental createRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    @CachePut(key = "#result.id")
    public Rental updateRental(Rental rental) {
        if (!rentalRepository.existsById(rental.getId()))
            throw new RentalNotFoundException(rental.getId());

        return rentalRepository.save(rental);
    }

    @CacheEvict(key = "#id")
    public void deleteRental(long id) {
        if (!rentalRepository.existsById(id))
            throw new RentalNotFoundException(id);

        rentalRepository.deleteById(id);
    }

    public boolean rentalExistsById(long id) {
        return rentalRepository.findById(id).isPresent();
    }

}
