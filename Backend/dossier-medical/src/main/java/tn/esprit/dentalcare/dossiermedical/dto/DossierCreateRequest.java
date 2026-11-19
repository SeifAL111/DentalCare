package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DossierCreateRequest(
        @Schema(description = "Identifiant du patient (Keycloak)", example = "patient-nadia-karoui")
        @NotBlank(message = "L'identifiant du patient est obligatoire")
        @Size(max = 64, message = "L'identifiant du patient ne doit pas dépasser 64 caractères")
        String patientId,

        @Schema(example = "A+")
        @Pattern(regexp = ValidationPatterns.GROUPE_SANGUIN, message = "Groupe sanguin invalide (ex. A+, O-, AB+)")
        String groupeSanguin,

        @Schema(example = "Dr Hela Mansour")
        @Size(max = 120)
        String medecinTraitant,

        @Schema(example = "Sami Karoui (conjoint)")
        @Size(max = 120)
        String contactUrgenceNom,

        @Schema(example = "+216 22 123 456")
        @Pattern(regexp = ValidationPatterns.TELEPHONE, message = "Numéro de téléphone invalide")
        String contactUrgenceTelephone,

        @Schema(example = "Patiente anxieuse, préfère les rendez-vous du matin.")
        @Size(max = 2000)
        String observations) {
}
