package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Booking;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    boolean existsByBookingAndTypeAndStatusNot(Booking booking, InspectionType type, InspectionStatus status);
    List<Inspection> findByBooking(Booking booking);
}
