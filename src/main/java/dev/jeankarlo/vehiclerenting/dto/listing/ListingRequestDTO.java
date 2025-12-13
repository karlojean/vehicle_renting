package dev.jeankarlo.vehiclerenting.dto.listing;

import dev.jeankarlo.vehiclerenting.dto.location.LocationRequestDTO;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ListingRequestDTO (
        @NotNull
        Long vehicleId,

        @NotNull
        @Digits(integer = 10, fraction = 2)
        Double pricePerDay,

        @NotEmpty
        String description,

        @NotNull
        LocationRequestDTO location
) {
}

