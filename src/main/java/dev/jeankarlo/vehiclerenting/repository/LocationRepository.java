package dev.jeankarlo.vehiclerenting.repository;

import dev.jeankarlo.vehiclerenting.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
