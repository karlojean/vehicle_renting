package dev.jeankarlo.vehiclerenting.dto.account;

import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AccountCreateDTO(
        @NotEmpty(message = "Nome de Usuário não pode ser vazio")
        String username,

        @NotEmpty(message = "Email não pode ser vazio")
        @Email(message = "Email deve ser válido")
        String email,

        @Size(min = 10, max = 11, message = "Número de telefone deve conter entre 10 e 11 dígitos")
        String phoneNumber,

        @NotEmpty(message = "Senha não pode ser vazia")
        @Size(min = 6, message = "Senha deve conter no mínimo 6 caracteres")
        String password,

        @NotEmpty(message = "Função da conta não pode ser vazia")
        AccountRole role
) {
}
