package de.teamg.antique.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 10)
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

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Rental> rental = new HashSet<>();

}
