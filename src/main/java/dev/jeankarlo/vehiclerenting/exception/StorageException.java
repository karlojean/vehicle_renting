package dev.jeankarlo.vehiclerenting.exception;

import org.springframework.http.HttpStatus;

public class StorageException extends BusinessException {
    public StorageException() {
        super(
                "Ocorreu um erro ao processar o arquivo.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
