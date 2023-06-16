package de.teamg.antique.data.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class CarNotFoundException extends ResponseStatusException {

    public CarNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, "The car with the specified ID '%s' was not found.".formatted(id));
    }

}