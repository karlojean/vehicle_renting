package dev.jeankarlo.vehiclerenting.dto.inspection.inspectionMedia;

import java.util.UUID;

public record InspectionMediaResponseDTO(
        UUID id,
        String originalFilename,
        String contentType,
        String extension,
        String url
) {
}
