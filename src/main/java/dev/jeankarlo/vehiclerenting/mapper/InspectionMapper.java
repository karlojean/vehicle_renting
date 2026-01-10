package dev.jeankarlo.vehiclerenting.mapper;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Inspection;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InspectionMapper {
    InspectionResponseDTO toResponseDTO(Inspection inspection);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "inspectionDate", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "status", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInspection(@MappingTarget Inspection inspection, InspectionPatchDTO inspectionPatchDTO);
}
