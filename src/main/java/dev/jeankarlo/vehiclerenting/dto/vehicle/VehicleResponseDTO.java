package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;

import java.time.Instant;

public record VehicleResponseDTO (
        Long id,
        String brand,
        String model,
        VehicleFuelType fuelType,
        VehicleType vehicleType,
        Integer yearManufactured,
        String licensePlate,
        String color,
        boolean isActive,
        Instant createdAt
) {}
