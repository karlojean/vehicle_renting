package dev.jeankarlo.vehiclerenting.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Size(max = 50)
    @NotNull
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Size(max = 50)
    @NotNull
    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Size(max = 10)
    @NotNull
    @Column(name = "pin_code", nullable = false, length = 10)
    private String pinCode;

    @Size(max = 50)
    @NotNull
    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;
}