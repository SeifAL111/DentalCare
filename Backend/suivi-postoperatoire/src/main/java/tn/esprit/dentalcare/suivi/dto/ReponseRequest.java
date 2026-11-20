package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requête de soumission d'une réponse par le patient")
public class ReponseRequest {

    @NotNull(message = "Le jour est obligatoire")
    @Min(value = 1, message = "Le jour doit être au minimum 1 (ex: J+1)")
    @Schema(description = "Numéro du jour post-opératoire (ex: 1 pour J+1, 3 pour J+3)", example = "1")
    private Integer jour;

    @NotNull(message = "L'identifiant de la question est obligatoire")
    @Schema(description = "Identifiant de la question du protocole", example = "1")
    private Long questionId;

    @NotNull(message = "La valeur de la réponse est obligatoire")
    @Min(value = 0, message = "La valeur minimale est 0")
    @Max(value = 10, message = "La valeur maximale est 10")
    @Schema(description = "Note évaluée par le patient (0 à 10)", example = "3")
    private Integer valeur;

    public ReponseRequest() {
    }

    public ReponseRequest(Integer jour, Long questionId, Integer valeur) {
        this.jour = jour;
        this.questionId = questionId;
        this.valeur = valeur;
    }

    public Integer getJour() {
        return jour;
    }

    public void setJour(Integer jour) {
        this.jour = jour;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }
}
