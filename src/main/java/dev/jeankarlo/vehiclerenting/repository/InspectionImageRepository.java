package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.InspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionImageRepository extends JpaRepository<InspectionImage, Long> {
}
