package dev.jeankarlo.vehiclerenting.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dev.jeankarlo.vehiclerenting.dto.location.LocationRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.location.LocationResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationRequestDTO dto);

    LocationResponseDTO toResponseDTO(Location location);
}
