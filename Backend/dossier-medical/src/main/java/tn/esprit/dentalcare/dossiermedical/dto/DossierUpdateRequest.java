package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Le patientId ne se modifie pas. La version lue doit être renvoyée :
 * si quelqu'un a modifié le dossier entre-temps, la mise à jour est refusée (409).
 */
public record DossierUpdateRequest(
        @Schema(description = "Version lue lors du GET (verrou optimiste)", example = "0")
        @NotNull(message = "La version du dossier est obligatoire")
        Long version,

        @Schema(example = "A+")
        @Pattern(regexp = ValidationPatterns.GROUPE_SANGUIN, message = "Groupe sanguin invalide (ex. A+, O-, AB+)")
        String groupeSanguin,

        @Size(max = 120)
        String medecinTraitant,

        @Size(max = 120)
        String contactUrgenceNom,

        @Pattern(regexp = ValidationPatterns.TELEPHONE, message = "Numéro de téléphone invalide")
        String contactUrgenceTelephone,

        @Size(max = 2000)
        String observations) {
}
