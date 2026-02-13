package dev.jeankarlo.vehiclerenting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    @EntityGraph(attributePaths = { "location", "mediaAssets", "mediaAssets.mediaAsset" })
    Page<Vehicle> findByPartner(Account owner, Pageable pageable);

    @EntityGraph(attributePaths = { "location", "mediaAssets", "mediaAssets.mediaAsset" })
    Optional<Vehicle> findByIdAndPartner_Id(Long id, Long ownerId);

    @Override
    @EntityGraph(attributePaths = { "location", "mediaAssets", "mediaAssets.mediaAsset" })
    List<Vehicle> findAll(Specification<Vehicle> spec);
}
