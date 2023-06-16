package de.teamg.antique.data.dto;

import java.sql.Timestamp;

public record RentalDTO(
        long id,
        long carId,
        long customerPersonId,
        long employeePersonId,
        Timestamp rentalStart,
        Timestamp rentalEnd,
        int kmStart,
        int kmEnd,
        double pricePerDay,
        double pricePerKm
) {
}
