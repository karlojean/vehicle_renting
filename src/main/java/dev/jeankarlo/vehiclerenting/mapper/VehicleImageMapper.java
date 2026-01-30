package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleImage.VehicleImageResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleImageMapper {
    VehicleImageResponseDTO toResponseDTO(VehicleImage vehicleImage);
}
