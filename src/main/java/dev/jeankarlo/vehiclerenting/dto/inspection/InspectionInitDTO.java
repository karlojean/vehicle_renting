package dev.jeankarlo.vehiclerenting.dto.inspection;


import dev.jeankarlo.vehiclerenting.entity.Inspection;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;

public record InspectionInitDTO(
        Long bookingId,
        InspectionType type
) {
}
