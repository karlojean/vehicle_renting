package dev.jeankarlo.vehiclerenting.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @Email(message = "Email deve ser válido")
        @NotEmpty(message = "Email não pode ser vazio")
        String email,

        @NotEmpty(message = "Senha não pode ser vazia")
        String password
) {
}
