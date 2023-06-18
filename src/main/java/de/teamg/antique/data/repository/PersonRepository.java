package de.teamg.antique.data.repository;

import de.teamg.antique.data.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query(value = "SELECT p FROM Person p WHERE NOT p.employee")
    List<Person> findCustomers();

    @Query(value = "SELECT p FROM Person p WHERE p.employee")
    List<Person> findEmployees();

    @Query(value = "SELECT p FROM Person p WHERE concat(lower(p.firstName), lower(p.lastName), lower(p.street), lower(p.city), lower(p.postCode), lower(p.country), p.dateOfBirth, lower(p.phone)) LIKE lower(concat('%', :filter, '%'))")
    List<Person> findAllFilterAllColumns(String filter);

    @Query(value = "SELECT p FROM Person p WHERE concat(lower(p.firstName), lower(p.lastName), lower(p.street), lower(p.city), lower(p.postCode), lower(p.country), p.dateOfBirth, lower(p.phone)) LIKE lower(concat('%', :filter, '%')) AND NOT p.employee")
    List<Person> findCustomersFilterAllColumns(String filter);

    @Query(value = "SELECT p FROM Person p WHERE concat(lower(p.firstName), lower(p.lastName), lower(p.street), lower(p.city), lower(p.postCode), lower(p.country), p.dateOfBirth, lower(p.phone)) LIKE lower(concat('%', :filter, '%')) AND p.employee")
    List<Person> findEmployeesFilterAllColumns(String filter);

    @Query(value = "SELECT CASE WHEN count(p) > 0 THEN true ELSE false END FROM Person p WHERE p.id = :id AND NOT p.employee")
    boolean customerExistsById(long id);

    @Query(value = "SELECT CASE WHEN count(p) > 0 THEN true ELSE false END FROM Person p WHERE p.id = :id AND p.employee")
    boolean employeeExistsById(long id);

}
