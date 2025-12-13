package dev.jeankarlo.vehiclerenting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.jeankarlo.vehiclerenting.entity.Listing;

public interface ListingRepository extends JpaRepository<Listing, Long> {
}
