package dev.jeankarlo.vehiclerenting.dto.auth;

public record LoginRequestDTO (
    String email,
    String password
){
}
