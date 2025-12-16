package dev.jeankarlo.vehiclerenting.dto.location;

public record LocationResponseDTO(
    String addressLine,
    String city,
    String state,
    String pinCode,
    String country,
    Double latitude,
    Double longitude
) {
}
