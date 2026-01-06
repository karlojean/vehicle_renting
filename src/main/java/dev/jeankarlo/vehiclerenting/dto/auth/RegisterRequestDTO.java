package dev.jeankarlo.vehiclerenting.dto.auth;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotEmpty(message = "Nome de Usuário não pode ser vazio")
        String username,

        @Email(message = "Email deve ser válido")
        @NotEmpty(message = "Email não pode ser vazio")
        String email,

        @NotEmpty(message = "Senha não pode ser vazia")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String password,

        @NotNull(message = "Função da conta não pode ser nula")
        AccountRole role
) {

    public AccountCreateDTO toAccountCreateDTO() {
        return new AccountCreateDTO(
                this.username,
                this.email,
                "",
                this.password,
                this.role
        );
    }

}
