package dev.jeankarlo.vehiclerenting.dto.location.inspectionMedia;

import java.util.UUID;

public record InspectionMediaResponseDTO(
        UUID id,
        String originalFilename,
        String contentType,
        String extension,
        String url
) {
}
