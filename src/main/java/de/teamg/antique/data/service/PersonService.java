package de.teamg.antique.data.service;

import de.teamg.antique.data.entity.Person;
import de.teamg.antique.data.exception.PersonNotFoundException;
import de.teamg.antique.data.repository.PersonRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"persons"})
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public List<Person> getAllCustomers() {
        return personRepository.findCustomers();
    }

    public List<Person> getAllEmployees() {
        return personRepository.findEmployees();
    }

    public List<Person> getAllPersonsFiltered(String filter) {
        return personRepository.findAllFilterAllColumns(filter);
    }

    public List<Person> getAllCustomersFiltered(String filter) {
        return personRepository.findCustomersFilterAllColumns(filter);
    }

    public List<Person> getAllEmployeesFiltered(String filter) {
        return personRepository.findEmployeesFilterAllColumns(filter);
    }

    @Cacheable(key = "#id")
    public Person getPersonById(long id) {
        return personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    }

    @CachePut(key = "#person.id")
    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    @CachePut(key = "#person.id")
    public Person updatePerson(Person person) {
        if (!personRepository.existsById(person.getId()))
            throw new PersonNotFoundException(person.getId());

        return personRepository.save(person);
    }

    @CacheEvict(key = "#id")
    public void deletePerson(long id) {
        if (!personRepository.existsById(id))
            throw new PersonNotFoundException(id);

        personRepository.deleteById(id);
    }

    public boolean personExistsById(long id) {
        return personRepository.existsById(id);
    }

    public boolean customerExistsById(long id) {
        return personRepository.customerExistsById(id);
    }

    public boolean employeeExistsById(long id) {
        return personRepository.employeeExistsById(id);
    }

}
