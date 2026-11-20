package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Données complètes d'un protocole post-opératoire")
public class ProtocoleResponse {

    @Schema(description = "Identifiant unique du protocole", example = "1")
    private Long id;

    @Schema(description = "Code de l'acte dentaire", example = "DEVITALISATION")
    private String acteCode;

    @Schema(description = "Jours de suivi", example = "[1, 3]")
    private List<Integer> jours;

    @Schema(description = "Consignes associées", example = "Prendre les antalgiques prescrits...")
    private String consignes;

    @Schema(description = "Questions rattachées au protocole")
    private List<QuestionResponse> questions;

    public ProtocoleResponse() {
    }

    public ProtocoleResponse(Long id, String acteCode, List<Integer> jours, String consignes, List<QuestionResponse> questions) {
        this.id = id;
        this.acteCode = acteCode;
        this.jours = jours;
        this.consignes = consignes;
        this.questions = questions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<QuestionResponse> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionResponse> questions) {
        this.questions = questions;
    }
}
