package dev.jeankarlo.vehiclerenting.dto.inspection;

import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import jakarta.validation.constraints.NotNull;

public record InspectionInitDTO(
        @NotNull(message = "Booking ID e obrigatorio")
        Long bookingId,

        @NotNull(message = "Tipo de inspeção e obrigatorio")
        InspectionType type
) {
}
