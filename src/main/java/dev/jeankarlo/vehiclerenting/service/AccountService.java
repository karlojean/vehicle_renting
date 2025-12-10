package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;

public interface AccountService {
    AccountResponseDTO create(AccountCreateDTO accountCreateDTO);
    AccountResponseDTO getById(Long id);
    AccountResponseDTO getByUsername(String username);

    Account getEntityById(Long id);
}
