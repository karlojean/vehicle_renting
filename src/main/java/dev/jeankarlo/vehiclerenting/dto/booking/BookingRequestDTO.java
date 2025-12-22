package dev.jeankarlo.vehiclerenting.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequestDTO(
        @NotNull
        Long vehicleId,

        @FutureOrPresent
        @NotNull
        LocalDate startDate,

        @Future
        @NotNull
        LocalDate endDate
) {
}

