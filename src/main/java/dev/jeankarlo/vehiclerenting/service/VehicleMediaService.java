package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VehicleMediaService {
    VehicleMediaResponseDTO uploadMedia(Long vehicleId, Long partnerId, MultipartFile file);
    List<VehicleMediaResponseDTO> getVehicleMedias(Long vehicleId);
    void deleteMedia(Long vehicleId, UUID vehicleMediaId, Long partnerId);
}
