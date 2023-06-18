package de.teamg.antique.data.dto;

import java.time.LocalDate;

public record RentalDTO(
        long id,
        LocalDate rentalStart,
        LocalDate rentalEnd,
        int kmStart,
        int kmEnd,
        double pricePerDay,
        double pricePerKm,
        long carId,
        long customerPersonId,
        long employeePersonId
) {
}
