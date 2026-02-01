package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia.VehicleMediaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VehicleMediaService {
    VehicleMediaResponseDTO uploadMedia(Long vehicleId, Long partnerId, MultipartFile file);
    List<VehicleMediaResponseDTO> getVehicleMedias(Long vehicleId);
}
