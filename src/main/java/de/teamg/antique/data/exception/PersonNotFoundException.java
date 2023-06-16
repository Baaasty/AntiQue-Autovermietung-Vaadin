package de.teamg.antique.data.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class PersonNotFoundException extends ResponseStatusException {

    public PersonNotFoundException(long id) {
        super(HttpStatus.NOT_FOUND, "The person with the specified ID '%s' was not found.".formatted(id));
    }

}