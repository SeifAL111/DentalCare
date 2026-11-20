package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "Requête de création d'un protocole post-opératoire")
public class ProtocoleRequest {

    @NotBlank(message = "Le code de l'acte est obligatoire")
    @Schema(description = "Code unique de l'acte dentaire", example = "DEVITALISATION")
    private String acteCode;

    @NotNull(message = "La liste des jours est obligatoire")
    @NotEmpty(message = "Au moins un jour de suivi doit être spécifié")
    @Schema(description = "Jours post-opératoires où le patient doit répondre", example = "[1, 3]")
    private List<Integer> jours;

    @NotBlank(message = "Les consignes sont obligatoires")
    @Schema(description = "Consignes et conseils post-opératoires", example = "Prendre les antalgiques prescrits en cas de douleur. Éviter de mâcher du côté traité pendant 48 heures. Brossage délicat.")
    private String consignes;

    @Valid
    @NotNull(message = "Les questions sont obligatoires")
    @NotEmpty(message = "Au moins une question doit être associée au protocole")
    @Schema(description = "Liste des questions de suivi")
    private List<QuestionRequest> questions;

    public ProtocoleRequest() {
    }

    public ProtocoleRequest(String acteCode, List<Integer> jours, String consignes, List<QuestionRequest> questions) {
        this.acteCode = acteCode;
        this.jours = jours;
        this.consignes = consignes;
        this.questions = questions;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public List<Integer> getJours() {
        return jours;
    }

    public void setJours(List<Integer> jours) {
        this.jours = jours;
    }

    public String getConsignes() {
        return consignes;
    }

    public void setConsignes(String consignes) {
        this.consignes = consignes;
    }

    public List<QuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRequest> questions) {
        this.questions = questions;
    }
}
