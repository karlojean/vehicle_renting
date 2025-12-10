package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VehiclePatchDTO(
        String brand,
        VehicleType type,
        String model,
        VehicleFuelType fuelType
) {
}
