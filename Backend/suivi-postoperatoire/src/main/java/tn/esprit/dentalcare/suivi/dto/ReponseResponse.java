package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.dentalcare.suivi.entity.StatutSuivi;
import java.time.LocalDateTime;

@Schema(description = "Confirmation de l'enregistrement de la réponse du patient")
public class ReponseResponse {

    @Schema(description = "Identifiant de la réponse", example = "1")
    private Long id;

    @Schema(description = "Jour de la réponse", example = "1")
    private int jour;

    @Schema(description = "Valeur renseignée", example = "8")
    private int valeur;

    @Schema(description = "Date et heure de réception", example = "2026-11-19T21:35:00")
    private LocalDateTime recueLe;

    @Schema(description = "Identifiant du suivi", example = "1")
    private Long suiviId;

    @Schema(description = "Identifiant de la question", example = "1")
    private Long questionId;

    @Schema(description = "Libellé de la question", example = "Quelle est l'intensité de votre douleur de 0 à 10 ?")
    private String questionLibelle;

    @Schema(description = "Indique si une alerte a été déclenchée suite à cette réponse", example = "true")
    private boolean alerteCreee;

    @Schema(description = "Motif de l'alerte créée si seuil dépassé", example = "Douleur 8 à J+1 (seuil 7)")
    private String motifAlerte;

    @Schema(description = "Statut actuel du suivi suite à la réponse", example = "EN_ALERTE")
    private StatutSuivi nouveauStatutSuivi;

    public ReponseResponse() {
    }

    public ReponseResponse(Long id, int jour, int valeur, LocalDateTime recueLe, Long suiviId, Long questionId, String questionLibelle, boolean alerteCreee, String motifAlerte, StatutSuivi nouveauStatutSuivi) {
        this.id = id;
        this.jour = jour;
        this.valeur = valeur;
        this.recueLe = recueLe;
        this.suiviId = suiviId;
        this.questionId = questionId;
        this.questionLibelle = questionLibelle;
        this.alerteCreee = alerteCreee;
        this.motifAlerte = motifAlerte;
        this.nouveauStatutSuivi = nouveauStatutSuivi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public LocalDateTime getRecueLe() {
        return recueLe;
    }

    public void setRecueLe(LocalDateTime recueLe) {
        this.recueLe = recueLe;
    }

    public Long getSuiviId() {
        return suiviId;
    }

    public void setSuiviId(Long suiviId) {
        this.suiviId = suiviId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionLibelle() {
        return questionLibelle;
    }

    public void setQuestionLibelle(String questionLibelle) {
        this.questionLibelle = questionLibelle;
    }

    public boolean isAlerteCreee() {
        return alerteCreee;
    }

    public void setAlerteCreee(boolean alerteCreee) {
        this.alerteCreee = alerteCreee;
    }

    public String getMotifAlerte() {
        return motifAlerte;
    }

    public void setMotifAlerte(String motifAlerte) {
        this.motifAlerte = motifAlerte;
    }

    public StatutSuivi getNouveauStatutSuivi() {
        return nouveauStatutSuivi;
    }

    public void setNouveauStatutSuivi(StatutSuivi nouveauStatutSuivi) {
        this.nouveauStatutSuivi = nouveauStatutSuivi;
    }
}
