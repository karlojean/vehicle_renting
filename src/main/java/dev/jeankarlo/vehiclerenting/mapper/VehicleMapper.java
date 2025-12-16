package dev.jeankarlo.vehiclerenting.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import dev.jeankarlo.vehiclerenting.dto.vehicle.VehiclePatchDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.vehicle.VehicleResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Vehicle;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Vehicle toEntity(VehicleRequestDTO dto);

    VehicleResponseDTO toResponseDTO(Vehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateVehicle(@MappingTarget Vehicle vehicle, VehiclePatchDTO vehiclePatchDTO);

}
