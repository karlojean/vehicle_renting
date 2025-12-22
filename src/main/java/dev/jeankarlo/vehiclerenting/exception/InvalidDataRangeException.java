package dev.jeankarlo.vehiclerenting.exception;

import org.springframework.http.HttpStatus;

public class InvalidDataRangeException extends BusinessException {
    public InvalidDataRangeException(String message) {
        super(
            message,
            HttpStatus.BAD_REQUEST
        );
    }
}
