package dev.jeankarlo.vehiclerenting.dto.vehicle;

import dev.jeankarlo.vehiclerenting.entity.enums.VehicleFuelType;
import dev.jeankarlo.vehiclerenting.entity.enums.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VehicleCreateDTO(
        @NotEmpty
        String brand,

        @NotEmpty
        String model,

        @NotNull
        VehicleFuelType fuelType,

        @NotNull
        VehicleType vehicleType,

        @NotNull
        Integer yearManufactured,

        @NotNull
        String licensePlate,

        @Size(max = 30)
        String color
) {
}
