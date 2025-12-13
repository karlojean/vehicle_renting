package dev.jeankarlo.vehiclerenting.dto.error;

import java.time.Instant;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ApiError {
  private Instant timestamp;
  private Integer status;
  private String error;
  private String message;
  private String path;
}
