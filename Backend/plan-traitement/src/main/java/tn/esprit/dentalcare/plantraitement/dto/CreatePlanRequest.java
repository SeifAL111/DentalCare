package tn.esprit.dentalcare.plantraitement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreatePlanRequest(
        @NotBlank String patientId,
        @NotEmpty @Valid List<EtapeRequest> etapes
) {
}
