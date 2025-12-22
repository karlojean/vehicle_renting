package dev.jeankarlo.vehiclerenting.exception;

import org.springframework.http.HttpStatus;

public class VehicleUnavailableException extends BusinessException {
    public VehicleUnavailableException() {
        super(
                "O Veiculo já possui reservares ativas para o período selecionado.",
                HttpStatus.CONFLICT
        );
    }
}
