package dev.jeankarlo.vehiclerenting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "fuel_type", nullable = false, length = 50)
    private String fuelType;

}