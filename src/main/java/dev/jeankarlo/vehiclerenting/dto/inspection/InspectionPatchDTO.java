package dev.jeankarlo.vehiclerenting.dto.inspection;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record InspectionPatchDTO(
        Integer odometerReading,

        @Min(value = 0, message = "Nível de combustível deve ser no mínimo 0")
        @Max(value = 100, message = "Nível de combustível deve ser no máximo 100")
        Integer fuelLevel,


        Boolean isCleanExterior,
        Boolean isCleanInterior,
        Boolean hasSmokeSmell,
        Boolean hasSpareTire,
        Boolean hasDocuments
) {
}

