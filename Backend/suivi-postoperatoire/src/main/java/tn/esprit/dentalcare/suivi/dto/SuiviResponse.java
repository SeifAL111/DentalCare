package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.dentalcare.suivi.entity.StatutSuivi;
import java.time.LocalDateTime;

@Schema(description = "Données renvoyées d'un suivi post-opératoire")
public class SuiviResponse {

    @Schema(description = "Identifiant du suivi", example = "1")
    private Long id;

    @Schema(description = "Identifiant du patient", example = "PATIENT-AHMED")
    private String patientId;

    @Schema(description = "Identifiant de l'acte", example = "101")
    private Long acteId;

    @Schema(description = "Indique si le protocole de suivi est renforcé", example = "false")
    private boolean renforce;

    @Schema(description = "Statut actuel du suivi", example = "ACTIF")
    private StatutSuivi statut;

    @Schema(description = "Identifiant du protocole associé", example = "1")
    private Long protocoleId;

    @Schema(description = "Code de l'acte dentaire du protocole", example = "DEVITALISATION")
    private String acteCode;

    @Schema(description = "Date et heure d'ouverture du suivi", example = "2026-11-19T21:30:00")
    private LocalDateTime dateOuverture;

    public SuiviResponse() {
    }

    public SuiviResponse(Long id, String patientId, Long acteId, boolean renforce, StatutSuivi statut, Long protocoleId, String acteCode, LocalDateTime dateOuverture) {
        this.id = id;
        this.patientId = patientId;
        this.acteId = acteId;
        this.renforce = renforce;
        this.statut = statut;
        this.protocoleId = protocoleId;
        this.acteCode = acteCode;
        this.dateOuverture = dateOuverture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isRenforce() {
        return renforce;
    }

    public void setRenforce(boolean renforce) {
        this.renforce = renforce;
    }

    public StatutSuivi getStatut() {
        return statut;
    }

    public void setStatut(StatutSuivi statut) {
        this.statut = statut;
    }

    public Long getProtocoleId() {
        return protocoleId;
    }

    public void setProtocoleId(Long protocoleId) {
        this.protocoleId = protocoleId;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }
}
