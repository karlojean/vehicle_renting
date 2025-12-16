package dev.jeankarlo.vehiclerenting.dto.location;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LocationRequestDTO(
        @NotEmpty
        @Size(max = 255)
        String addressLine,

        @NotEmpty
        @Size(max = 50)
        String city,

        @NotEmpty
        @Size(max = 50)
        String state,

        @NotEmpty
        @Size(max = 10)
        String pinCode,

        @NotEmpty
        @Size(max = 50)
        String country,

        Double latitude,

        Double longitude
) {
}
