package dev.jeankarlo.vehiclerenting.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.InspectionMedia;

public interface InspectionMediaRepository extends JpaRepository<InspectionMedia, UUID> {
    List<InspectionMedia> findAllByInspection(Inspection inspection);

    Optional<InspectionMedia> findByIdAndInspection(UUID id, Inspection inspection);

    int countByInspection(Inspection inspection);
}
