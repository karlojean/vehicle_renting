package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.InspectionMedia;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;
import dev.jeankarlo.vehiclerenting.entity.VehicleMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InspectionMediaRepository extends JpaRepository<InspectionMedia, UUID> {
    List<InspectionMedia> findAllByInspection(Inspection inspection);
}
