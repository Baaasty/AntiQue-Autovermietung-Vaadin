package de.teamg.antique.data.controller;

import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.entity.Rental;
import de.teamg.antique.data.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/persons")
public class PersonController {

    private final PersonService personService;

    private final HttpServletRequest httpServletRequest;

    public PersonController(PersonService personService, HttpServletRequest httpServletRequest) {
        this.personService = personService;
        this.httpServletRequest = httpServletRequest;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPerson(@PathVariable long id) {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity
                .created(URI.create(httpServletRequest.getRequestURI()))
                .body(personService.createPerson(person));
    }

    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.updatePerson(person));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable long id) {
        personService.deletePerson(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/rentals")
    public ResponseEntity<Set<Rental>> getRentalsFromPerson(@PathVariable long id, @RequestParam(required = false) boolean employee) {
        Person person = personService.getPersonById(id);

        return ResponseEntity.ok(
                employee ?
                        person.getEmployeeRental() :
                        person.getCustomerRental()
        );
    }

}
