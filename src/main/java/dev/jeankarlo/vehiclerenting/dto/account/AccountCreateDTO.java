package dev.jeankarlo.vehiclerenting.dto.account;

import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AccountCreateDTO(
        @NotEmpty
        String username,

        @NotEmpty
        @Email
        String email,

        @Size(min = 10, max = 11)
        String phoneNumber,

        @NotEmpty
        String password,

        @NotEmpty
        AccountRole role
) {
}
