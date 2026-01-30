package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleMediaRepository extends JpaRepository<VehicleMedia, UUID> {
}
