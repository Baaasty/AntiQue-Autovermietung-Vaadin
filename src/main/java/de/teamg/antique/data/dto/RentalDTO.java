package de.teamg.antique.data.dto;

import java.time.LocalDateTime;

public record RentalDTO(
        long id,
        LocalDateTime rentalStart,
        LocalDateTime rentalEnd,
        int kmStart,
        int kmEnd,
        double pricePerDay,
        double pricePerKm,
        long carId,
        long customerPersonId,
        long employeePersonId
) {
}
