package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;

public interface AccountService {
    AccountResponseDTO create(AccountCreateDTO accountCreateDTO);
    AccountResponseDTO getById(Long id);
}
