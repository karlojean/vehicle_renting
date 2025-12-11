package dev.jeankarlo.vehiclerenting.entity;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

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

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "fuel_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VehicleFuelType fuelType;

    @NotNull
    @Column(name = "vehicle_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @NotNull
    @Column(name = "year_manufactured", nullable = false)
    private Integer yearManufactured;

    @Size(max = 20)
    @NotNull
    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Size(max = 30)
    @Column(name = "color", length = 30)
    private String color;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;


    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;
}