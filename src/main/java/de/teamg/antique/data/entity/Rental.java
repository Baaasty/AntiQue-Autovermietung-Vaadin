package de.teamg.antique.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private Timestamp rentalStart;

    @Column
    private Timestamp rentalEnd;

    @Column(nullable = false)
    private int kmStart;

    @Column
    private int kmEnd;

    @Column(nullable = false)
    private double pricePerDay;

    @Column(nullable = false)
    private double pricePerKm;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Person customerPerson;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Person employeePerson;

    public Rental(
            Timestamp rentalStart,
            Timestamp rentalEnd,
            int kmStart,
            int kmEnd,
            double pricePerDay,
            double pricePerKm,
            Car car,
            Person customerPerson,
            Person employeePerson
    ) {
        this.rentalStart = rentalStart;
        this.rentalEnd = rentalEnd;
        this.kmStart = kmStart;
        this.kmEnd = kmEnd;
        this.pricePerDay = pricePerDay;
        this.pricePerKm = pricePerKm;
        this.car = car;
        this.customerPerson = customerPerson;
        this.employeePerson = employeePerson;
    }
}
