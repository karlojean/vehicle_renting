package dev.jeankarlo.vehiclerenting.dto.vehicle.vehicleMedia;

import java.util.UUID;

public record VehicleMediaResponseDTO(
    UUID id,
    String originalFilename,
    String contentType,
    String extension,
    String url
) {
}
