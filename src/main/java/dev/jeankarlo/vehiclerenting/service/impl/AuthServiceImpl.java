package dev.jeankarlo.vehiclerenting.service.impl;

import dev.jeankarlo.vehiclerenting.dto.account.AccountCreateDTO;
import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.LoginRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.RegisterRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.TokenResponseDTO;
import dev.jeankarlo.vehiclerenting.entity.Account;
import dev.jeankarlo.vehiclerenting.repository.AccountRepository;
import dev.jeankarlo.vehiclerenting.service.AccountService;
import dev.jeankarlo.vehiclerenting.service.AuthService;
import dev.jeankarlo.vehiclerenting.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(AccountService accountService, AccountRepository accountRepository, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public AccountResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        AccountCreateDTO accountCreateDTO = registerRequestDTO.toAccountCreateDTO();

        AccountResponseDTO account = accountService.create(accountCreateDTO);

        // TODO: send welcome email

        return account;
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String email = normalizeEmail(loginRequestDTO.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, loginRequestDTO.password())
        );

        Account accountPrincipal = (Account) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(accountPrincipal);

        return new TokenResponseDTO(jwt);

    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
