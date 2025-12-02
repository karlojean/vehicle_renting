package dev.jeankarlo.vehiclerenting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "address_line", nullable = false, length = Integer.MAX_VALUE)
    private String addressLine;

    @Column(name = "address_line_optional", length = Integer.MAX_VALUE)
    private String addressLineOptional;

    @Column(name = "area", nullable = false, length = 100)
    private String area;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "pin_code", nullable = false, length = 10)
    private String pinCode;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

}