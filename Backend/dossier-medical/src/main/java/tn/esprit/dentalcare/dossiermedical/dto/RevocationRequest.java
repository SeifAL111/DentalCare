package tn.esprit.dentalcare.dossiermedical.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record RevocationRequest(
        @Schema(example = "Le patient souhaite réfléchir à une alternative")
        @Size(max = 500)
        String motif) {
}
