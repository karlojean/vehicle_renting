package dev.jeankarlo.vehiclerenting.entity;

import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

}