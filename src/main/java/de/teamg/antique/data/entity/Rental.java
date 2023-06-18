package de.teamg.antique.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private LocalDate rentalStart;

    @Column
    private LocalDate rentalEnd;

    @Column(nullable = false)
    private int kmStart;

    @Column
    private int kmEnd;

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

    @ManyToOne
    @JoinColumn(name = "car_id", referencedColumnName = "id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private Person customerPerson;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Person employeePerson;

    public Rental(
            LocalDate rentalStart,
            LocalDate rentalEnd,
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
