package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionInitDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionPatchDTO;
import dev.jeankarlo.vehiclerenting.dto.inspection.InspectionResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Inspection;

import java.util.List;

public interface InspectionService {
    InspectionResponseDTO initInspection(InspectionInitDTO inspectionInitDTO, Long partnerId);
    InspectionResponseDTO updateById(Long id, InspectionPatchDTO inspectionPatchDTO, Long partnerId) ;
    InspectionResponseDTO completeInspection(Long id, Long partnerId);
    InspectionResponseDTO cancelInspection(Long id, Long partnerId);
    List<InspectionResponseDTO> getInspectionsByBookingId(Long bookingId, Long partnerId);
    Inspection getInspectionEntityById(Long id);
    void validatePartnerOwnership(Long partnerId, Inspection inspection);
}
