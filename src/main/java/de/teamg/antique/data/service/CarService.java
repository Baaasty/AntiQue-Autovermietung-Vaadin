package de.teamg.antique.data.service;

import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.exception.CarNotFoundException;
import de.teamg.antique.data.repository.CarRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"cars"})
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getAllCarsFiltered(String filter) {
        return carRepository.findAllFilterAllColumns(filter);
    }

    @Cacheable(key = "#id")
    public Car getCarById(long id) {
        return carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
    }

    @CachePut(key = "#car.id")
    public Car createCar(Car car) {
        return carRepository.save(car);
    }

    @CachePut(key = "#car.id")
    public Car updateCar(Car car) {
        if (!carRepository.existsById(car.getId()))
            throw new CarNotFoundException(car.getId());

        return carRepository.save(car);
    }

    @CacheEvict(key = "#id")
    public void deleteCar(long id) {
        if (!carRepository.existsById(id))
            throw new CarNotFoundException(id);

        carRepository.deleteById(id);
    }

    public boolean carExistsById(long id) {
        return carRepository.findById(id).isPresent();
    }

}
