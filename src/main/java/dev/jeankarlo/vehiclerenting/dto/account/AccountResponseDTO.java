package dev.jeankarlo.vehiclerenting.dto.account;

import dev.jeankarlo.vehiclerenting.entity.enums.AccountRole;

public record AccountResponseDTO(
        Long id,
        String username,
        String email,
        String phoneNumber,
        AccountRole role
) {
}
