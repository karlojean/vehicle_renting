package dev.jeankarlo.vehiclerenting.dto.booking;

public record BookingResponseDTO(
    Long id,
    Long vehicleId,
    Long renterId,
    String startDate,
    String endDate,
    Long totalPriceCents,
    String status,
    String createdAt
) {
}
