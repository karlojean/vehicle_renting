package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;

public interface InspectionService {
    void initInspection(InspectionInitDTO inspectionInitDTO, Long ownerId);
}
