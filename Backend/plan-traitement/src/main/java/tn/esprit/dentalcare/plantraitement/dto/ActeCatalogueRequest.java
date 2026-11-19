package tn.esprit.dentalcare.plantraitement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ActeCatalogueRequest(
        @NotBlank String code,
        @NotBlank String libelle,
        @NotNull @Min(1) Integer dureeMinutes,
        @NotNull @DecimalMin("0.0") BigDecimal tarif,
        @NotNull @Min(0) Integer delaiCicatrisationJours,
        String competenceRequise
) {
}
