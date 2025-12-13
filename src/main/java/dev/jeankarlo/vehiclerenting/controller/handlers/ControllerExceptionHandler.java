package dev.jeankarlo.vehiclerenting.controller.handlers;

import java.time.Instant;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.jeankarlo.vehiclerenting.dto.error.ApiError;
import dev.jeankarlo.vehiclerenting.dto.error.ValidationError;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
      return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", request);
  }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> database(DataIntegrityViolationException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Violação de integridade de dados", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> jsonError(HttpMessageNotReadableException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "JSON inválido ou formato incorreto", request);
    }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
    String error = "Validation error";
    HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;

    ValidationError err = ValidationError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error(error)
            .message("Erro na validação dos campos")
            .path(request.getRequestURI())
            .build();

    for (FieldError f : e.getBindingResult().getFieldErrors()) {
      err.addError(f.getField(), f.getDefaultMessage());
    }

    return ResponseEntity.status(status).body(err);
  }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiError error = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }



}
