package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VehicleCreateDTO(
        @NotEmpty
        String brand,

        @NotNull
        VehicleType type,

        @NotEmpty
        String model,

        @NotNull
        VehicleFuelType fuelType
) {
}
