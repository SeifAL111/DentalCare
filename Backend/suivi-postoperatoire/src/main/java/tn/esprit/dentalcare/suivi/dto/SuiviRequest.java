package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requête d'ouverture d'un nouveau suivi post-opératoire")
public class SuiviRequest {

    @NotBlank(message = "L'identifiant du patient est obligatoire")
    @Schema(description = "Identifiant unique du patient", example = "PATIENT-AHMED")
    private String patientId;

    @NotNull(message = "L'identifiant de l'acte dentaire est obligatoire")
    @Schema(description = "Identifiant de l'acte dentaire réalisé", example = "101")
    private Long acteId;

    @NotNull(message = "L'identifiant du protocole est obligatoire")
    @Schema(description = "Identifiant du protocole post-opératoire à appliquer", example = "1")
    private Long protocoleId;

    public SuiviRequest() {
    }

    public SuiviRequest(String patientId, Long acteId, Long protocoleId) {
        this.patientId = patientId;
        this.acteId = acteId;
        this.protocoleId = protocoleId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Long getActeId() {
        return acteId;
    }

    public void setActeId(Long acteId) {
        this.acteId = acteId;
    }

    public Long getProtocoleId() {
        return protocoleId;
    }

    public void setProtocoleId(Long protocoleId) {
        this.protocoleId = protocoleId;
    }
}
