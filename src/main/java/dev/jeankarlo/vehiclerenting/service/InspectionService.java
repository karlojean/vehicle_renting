package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.entity.enums.InspectionType;
import org.springframework.web.multipart.MultipartFile;

public interface InspectionService {
    void initInspection(InspectionInitDTO inspectionInitDTO, Long partnerId);
    void uploadInspectionImage(Long inspectionId, MultipartFile file, Long partnerId);
}
