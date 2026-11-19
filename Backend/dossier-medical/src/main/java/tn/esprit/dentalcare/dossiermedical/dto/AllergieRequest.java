package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import tn.esprit.dentalcare.dossiermedical.domain.CategorieAllergie;
import tn.esprit.dentalcare.dossiermedical.domain.Severite;

import java.time.LocalDate;

public record AllergieRequest(
        @Schema(example = "Pénicilline")
        @NotBlank(message = "La substance est obligatoire")
        @Size(max = 120)
        String substance,

        @Schema(example = "MEDICAMENT")
        @NotNull(message = "La catégorie est obligatoire")
        CategorieAllergie categorie,

        @Schema(example = "ANAPHYLAXIE")
        @NotNull(message = "La sévérité est obligatoire")
        Severite severite,

        @Schema(example = "Œdème de Quincke après une prise d'amoxicilline en 2019")
        @Size(max = 500)
        String reaction,

        @Schema(example = "2019-06-12")
        @PastOrPresent(message = "La date de déclaration ne peut pas être dans le futur")
        LocalDate declareeLe) {
}
