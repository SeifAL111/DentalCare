package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerificationRequest(
        @Schema(description = "Médicament ou produit à contrôler", example = "Amoxicilline 1 g")
        @NotBlank(message = "Le médicament est obligatoire")
        @Size(max = 200)
        String medicament) {
}
