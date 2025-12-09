package dev.jeankarlo.vehiclerenting.dto.auth;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;

public record RegisterRequestDTO(
    String username,
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
