package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;

public record VehicleResponseDTO (
        Long id,
        String brand,
        VehicleType type,
        String model,
        VehicleFuelType fuelType
) {}
