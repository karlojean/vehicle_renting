package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.vehicleImage.VehicleImageResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.VehicleImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleImageMapper {
    VehicleImageResponseDTO toResponseDTO(VehicleImage vehicleImage);
}
