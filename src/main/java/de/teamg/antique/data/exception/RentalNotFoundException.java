package de.teamg.antique.data.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class RentalNotFoundException extends ResponseStatusException {

    public RentalNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, "The rental with the specified ID '%s' was not found.".formatted(id));
    }

}