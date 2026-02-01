package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleMediaRepository extends JpaRepository<VehicleMedia, UUID> {

    List<VehicleMedia> findAllByVehicle(Vehicle vehicle);

}
