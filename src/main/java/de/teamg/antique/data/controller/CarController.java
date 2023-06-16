package de.teamg.antique.data.controller;

import de.teamg.antique.data.entity.Car;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.service.CarService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/cars")
public class CarController {

    private final CarService carService;

    private final HttpServletRequest httpServletRequest;

    public CarController(CarService carService, HttpServletRequest httpServletRequest) {
        this.carService = carService;
        this.httpServletRequest = httpServletRequest;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCar(@PathVariable long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity
                .created(URI.create(httpServletRequest.getRequestURI()))
                .body(carService.createCar(car));
    }

    @PutMapping
    public ResponseEntity<Car> updateCar(@RequestBody Car car) {
        return ResponseEntity.ok(carService.updateCar(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Car> deleteCar(@PathVariable long id) {
        carService.deleteCar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<Set<Rental>> getRentalsFromPerson(@PathVariable long id) {
        return ResponseEntity.ok(carService.getCarById(id).getRental());
    }

}
