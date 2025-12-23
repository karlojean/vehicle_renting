package dev.jeankarlo.vehiclerenting.dto.vehicle;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VehicleSearchFilter(
        @NotBlank(message = "A Cidade é obrigatória")
        String city,

        @NotNull(message = "A data de início é obrigatória")
        @FutureOrPresent(message = "A data de início não deve ser no passado")
        LocalDate startDate,

        @NotNull(message = "A data de fim é obrigatória")
        @FutureOrPresent(message = "A data de fim não deve ser no passado")
        LocalDate endDate
) {

    @AssertTrue(message = "A data de fim deve ser posterior à data de início (pelo menos 1 dia de diferença)")
    public boolean isValidateRange() {
        if(startDate == null || endDate == null) {
            return true;
        }

        return startDate.isBefore(endDate);
    }
}
