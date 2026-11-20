package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Détails d'une alerte clinique")
public class AlerteResponse {

    @Schema(description = "Identifiant de l'alerte", example = "1")
    private Long id;

    @Schema(description = "Motif lisible de l'alerte", example = "Douleur 8 à J+3 (seuil 7)")
    private String motif;

    @Schema(description = "Date et heure de création", example = "2026-11-19T21:40:00")
    private LocalDateTime creeLe;

    @Schema(description = "Identifiant du suivi concerné", example = "1")
    private Long suiviId;

    @Schema(description = "Identifiant du patient concerné", example = "PATIENT-AHMED")
    private String patientId;

    public AlerteResponse() {
    }

    public AlerteResponse(Long id, String motif, LocalDateTime creeLe, Long suiviId, String patientId) {
        this.id = id;
        this.motif = motif;
        this.creeLe = creeLe;
        this.suiviId = suiviId;
        this.patientId = patientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(LocalDateTime creeLe) {
        this.creeLe = creeLe;
    }

    public Long getSuiviId() {
        return suiviId;
    }

    public void setSuiviId(Long suiviId) {
        this.suiviId = suiviId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
