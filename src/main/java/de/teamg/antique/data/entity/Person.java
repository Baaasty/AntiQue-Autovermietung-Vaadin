package de.teamg.antique.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class Person {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postCode;

    @Column(nullable = false, length = 3)
    private short countryId;

    @Column(nullable = false)
    private Date dateOfBirth;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private boolean employee;

    @OneToMany(mappedBy = "customerPerson", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rental> customerRental = new HashSet<>();

    @OneToMany(mappedBy = "employeePerson", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rental> employeeRental = new HashSet<>();

}
