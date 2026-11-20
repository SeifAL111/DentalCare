package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Données d'une question de suivi")
public class QuestionRequest {

    @NotBlank(message = "Le libellé de la question ne peut pas être vide")
    @Schema(description = "Libellé de la question", example = "Quelle est l'intensité de votre douleur de 0 à 10 ?")
    private String libelle;

    @NotNull(message = "Le seuil d'alerte est obligatoire")
    @Min(value = 0, message = "Le seuil doit être au minimum 0")
    @Max(value = 10, message = "Le seuil doit être au maximum 10")
    @Schema(description = "Seuil à partir duquel une alerte est levée (0-10)", example = "7")
    private Integer seuilAlerte;

    public QuestionRequest() {
    }

    public QuestionRequest(String libelle, Integer seuilAlerte) {
        this.libelle = libelle;
        this.seuilAlerte = seuilAlerte;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(Integer seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
