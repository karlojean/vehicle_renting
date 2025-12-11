package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import jakarta.validation.constraints.Size;

public record VehiclePatchDTO(
        String brand,
        String model,
        VehicleFuelType fuelType,
        VehicleType vehicleType,
        Integer yearManufactured,
        String licensePlate,
        @Size(max = 30)
        String color
) {
}
