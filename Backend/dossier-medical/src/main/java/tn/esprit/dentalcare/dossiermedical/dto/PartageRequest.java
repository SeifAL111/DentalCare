package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.esprit.dentalcare.dossiermedical.domain.PorteePartage;

public record PartageRequest(
        @Schema(description = "Praticien destinataire", example = "praticien-benali")
        @NotBlank(message = "Le praticien destinataire est obligatoire")
        @Size(max = 64)
        String praticienId,

        @Schema(description = "Durée de validité du lien, de 1 à 72 heures", example = "24")
        @NotNull(message = "La durée est obligatoire")
        @Min(value = 1, message = "La durée minimale est d'une heure")
        @Max(value = 72, message = "La durée maximale est de 72 heures")
        Integer dureeHeures,

        @Schema(example = "RESUME_MEDICAL")
        @NotNull(message = "La portée du partage est obligatoire")
        PorteePartage portee,

        @Schema(description = "Optionnel : nombre maximum de consultations", example = "3")
        @Min(value = 1, message = "Au moins une consultation")
        @Max(value = 50, message = "Au plus 50 consultations")
        Integer nbConsultationsMax,

        @Schema(example = "Avis du parodontiste avant greffe gingivale")
        @Size(max = 300)
        String motif) {
}
