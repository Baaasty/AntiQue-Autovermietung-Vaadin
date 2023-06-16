package de.teamg.antique.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 8)
    private String licensePlate;

    @Column(nullable = false, length = 20)
    private String designation;

    @Column(nullable = false)
    private int modelYears;

    @Column(nullable = false)
    private int hp;

    @Column(nullable = false)
    private int cc;

    @Column(nullable = false, length = 20)
    private String fuel;

    @Column(nullable = false, length = 20)
    private String insuranceNumber;

    @Column(nullable = false)
    private LocalDate tuv;

    @Column(nullable = false)
    private double pricePerDay;

    @Column(nullable = false)
    private double pricePerKm;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rental> rental = new HashSet<>();

}
