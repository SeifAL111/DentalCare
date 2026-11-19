package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;

public record ConsentementRequest(
        @Schema(example = "IMPLANT")
        @NotNull(message = "Le type d'acte est obligatoire")
        TypeActe typeActe,

        @Schema(description = "Patient ou représentant légal", example = "Nadia Karoui")
        @NotBlank(message = "Le signataire est obligatoire")
        @Size(max = 120)
        String signataire,

        @Schema(example = "praticien-moreau")
        @NotBlank(message = "Le praticien est obligatoire")
        @Size(max = 64)
        String praticienId,

        @Schema(example = "v1")
        @Size(max = 20)
        String versionDocument,

        @Schema(example = "J'ai été informée du déroulement de la pose d'implant, des risques "
                + "(infection, échec d'ostéo-intégration, atteinte nerveuse) et des alternatives.")
        @NotBlank(message = "Le texte d'information est obligatoire")
        @Size(max = 5000)
        String texteInforme) {
}
