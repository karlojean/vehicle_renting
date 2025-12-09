package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.account.AccountResponseDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.LoginRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.RegisterRequestDTO;
import dev.jeankarlo.vehiclerenting.dto.auth.TokenResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService {
    AccountResponseDTO register(RegisterRequestDTO registerRequestDTO);
    TokenResponseDTO login(LoginRequestDTO loginRequestDTO);
}
