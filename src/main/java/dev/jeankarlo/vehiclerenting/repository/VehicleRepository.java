package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findByOwner(Account owner, Pageable pageable);
    Optional<Vehicle> findByIdAndOwner_Id(Long id, Long ownerId);
}
