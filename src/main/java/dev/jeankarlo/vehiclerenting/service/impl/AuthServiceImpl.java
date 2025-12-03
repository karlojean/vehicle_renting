package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.RegisterDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;

    public AuthServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    public void register(RegisterDTO registerDTO) {
        AccountCreateDTO accountCreateDTO = registerDTO.toAccountCreateDTO();

        AccountResponseDTO account = accountService.create(accountCreateDTO);

        // TODO: send welcome email

        return;
    }

}
