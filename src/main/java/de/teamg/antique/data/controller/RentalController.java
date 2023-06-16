package de.teamg.antique.data.controller;

import de.teamg.antique.data.dto.RentalDTO;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.service.RentalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    private final HttpServletRequest httpServletRequest;

    public RentalController(RentalService rentalService, HttpServletRequest httpServletRequest) {
        this.rentalService = rentalService;
        this.httpServletRequest = httpServletRequest;
    }

    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody RentalDTO rentalDTO) {
        return ResponseEntity
                .created(URI.create(httpServletRequest.getRequestURI()))
                .body(rentalService.createRental(rentalDTO));
    }

    @PutMapping
    public ResponseEntity<Rental> updateRental(@RequestBody Rental rental) {
        return ResponseEntity.ok(rentalService.updateRental(rental));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Rental> deleteRental(@PathVariable long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.ok().build();
    }

}
