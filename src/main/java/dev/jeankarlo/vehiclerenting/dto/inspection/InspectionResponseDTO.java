package dev.jeankarlo.vehiclerenting.dto.inspection;


import dev.jeankarlo.vehiclerenting.entity.enums.InspectionStatus;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;

public record InspectionResponseDTO (
        Long id,
        InspectionStatus status,
        InspectionType type
) {
}
