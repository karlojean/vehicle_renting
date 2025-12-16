package dev.jeankarlo.vehiclerenting.dto.error;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ValidationError extends ApiError {

    @Builder.Default
    private List<FieldMessage> fieldMessages = new ArrayList<>();

    public void addError(String fieldName, String message) {
        fieldMessages.add(new FieldMessage(fieldName, message));
    }
}
