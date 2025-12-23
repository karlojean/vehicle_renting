package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.vehicle.id = :vehicleId
            AND (:startDate < b.endDate AND :endDate > b.startDate)
           """)
    boolean existsOverlap(Long vehicleId, LocalDate startDate, LocalDate endDate);

    List<Booking> findByVehicle_Owner_Id(Long ownerId);

}
