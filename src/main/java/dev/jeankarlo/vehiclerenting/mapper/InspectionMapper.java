package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InspectionMapper {
    InspectionResponseDTO toResponseDTO(Inspection inspection);
}
