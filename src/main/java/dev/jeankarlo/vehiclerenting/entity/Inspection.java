package dev.jeankarlo.vehiclerenting.entity;

import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inspection")
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "inspection_date", nullable = false)
    private Instant inspectionDate;

    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private InspectionType type;

    @Column(name = "comments", length = Integer.MAX_VALUE)
    private String comments;

    @NotNull
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private InspectionStatus status;

    @Column(name = "odometer_reading")
    private Integer odometerReading;

    @Column(name = "fuel_level")
    private Integer fuelLevel;

    @ColumnDefault("false")
    @Column(name = "is_clean_exterior")
    private Boolean isCleanExterior;

    @ColumnDefault("false")
    @Column(name = "is_clean_interior")
    private Boolean isCleanInterior;

    @ColumnDefault("false")
    @Column(name = "has_smoke_smell")
    private Boolean hasSmokeSmell;

    @ColumnDefault("true")
    @Column(name = "has_spare_tire")
    private Boolean hasSpareTire;

    @ColumnDefault("true")
    @Column(name = "has_documents")
    private Boolean hasDocuments;

}