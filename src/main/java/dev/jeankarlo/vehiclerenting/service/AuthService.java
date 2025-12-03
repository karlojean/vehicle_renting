package dev.jeankarlo.vehiclerenting.service;

import dev.jeankarlo.vehiclerenting.dto.auth.RegisterDTO;

public interface AuthService {
    void register(RegisterDTO registerDTO);
}
