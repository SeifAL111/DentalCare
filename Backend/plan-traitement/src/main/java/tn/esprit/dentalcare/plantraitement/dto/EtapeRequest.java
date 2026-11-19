package tn.esprit.dentalcare.plantraitement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EtapeRequest(
        @NotNull @Min(1) Integer ordre,
        @NotBlank String acteCode,
        Integer dent
) {
}
