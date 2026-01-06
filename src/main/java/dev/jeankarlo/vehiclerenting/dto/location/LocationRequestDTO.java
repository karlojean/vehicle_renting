package dev.jeankarlo.vehiclerenting.dto.location;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LocationRequestDTO(
        @NotEmpty(message = "Endereço não pode ser vazio")
        @Size(max = 255, message = "Endereço não pode exceder 255 caracteres")
        String addressLine,

        @NotEmpty(message = "Cidade não pode ser vazia")
        @Size(max = 50, message = "Cidade não pode exceder 50 caracteres")
        String city,

        @NotEmpty(message = "Estado não pode ser vazio")
        @Size(max = 50, message = "Estado não pode exceder 50 caracteres")
        String state,

        @NotEmpty(message = "Código postal não pode ser vazio")
        @Size(max = 10, message = "Código postal não pode exceder 10 caracteres")
        String pinCode,

        @NotEmpty(message = "País não pode ser vazio")
        @Size(max = 50, message = "País não pode exceder 50 caracteres")
        String country,

        Double latitude,

        Double longitude
) {
}
