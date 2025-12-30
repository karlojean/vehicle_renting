package dev.jeankarlo.vehiclerenting.dto.auth;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

public record RegisterRequestDTO(
    String username,
    @Email
    String email,
    String password,
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
