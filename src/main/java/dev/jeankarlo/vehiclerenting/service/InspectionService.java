package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.inspectionImage.InspectionImageRespondeDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InspectionService {
    InspectionResponseDTO initInspection(InspectionInitDTO inspectionInitDTO, Long partnerId);
    InspectionImageRespondeDTO uploadInspectionImage(Long inspectionId, MultipartFile file, Long partnerId);
    InspectionResponseDTO updateById(Long id, InspectionPatchDTO inspectionPatchDTO, Long partnerId) ;
    InspectionResponseDTO completeInspection(Long id, Long partnerId);
    InspectionResponseDTO cancelInspection(Long id, Long partnerId);
    List<InspectionResponseDTO> getInspectionsByBookingId(Long bookingId, Long partnerId);
}
