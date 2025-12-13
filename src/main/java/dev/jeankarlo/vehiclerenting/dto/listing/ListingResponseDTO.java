package dev.jeankarlo.vehiclerenting.dto.listing;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;

public record ListingResponseDTO(
        Long id,
        VehicleResponseDTO vehicle,
        Double pricePerDay,
        String description,
        String city,
        String state
) {
}
