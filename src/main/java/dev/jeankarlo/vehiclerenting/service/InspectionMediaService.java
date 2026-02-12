package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionMedia.InspectionMediaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface InspectionMediaService {
    InspectionMediaResponseDTO uploadMedia(Long inspectionId, Long partnerId, MultipartFile file);
    List<InspectionMediaResponseDTO> getMediasByInspectionId(Long inspectionId, Long partnerId);
    void deleteMedia(Long inspectionId, UUID inspectionMediaId, Long partnerId);
}
