package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.TypeAntecedent;

import java.time.LocalDate;

public record AntecedentRequest(
        @Schema(example = "TRAITEMENT_EN_COURS")
        @NotNull(message = "Le type d'antécédent est obligatoire")
        TypeAntecedent type,

        @Schema(example = "Apixaban 5 mg, deux fois par jour (fibrillation atriale)")
        @NotBlank(message = "La description est obligatoire")
        @Size(max = 500)
        String description,

        @Schema(description = "Optionnel : rend l'antécédent exploitable par les règles de risque",
                example = "ANTICOAGULANT")
        FacteurRisque facteurRisque,

        @Schema(example = "2024-03-01")
        @PastOrPresent(message = "La date de début ne peut pas être dans le futur")
        LocalDate dateDebut,

        @Schema(description = "Faux pour un traitement arrêté (vrai par défaut)", example = "true")
        Boolean actif) {
}
