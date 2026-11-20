package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Données renvoyées pour une question de suivi")
public class QuestionResponse {

    @Schema(description = "Identifiant unique de la question", example = "1")
    private Long id;

    @Schema(description = "Libellé de la question", example = "Quelle est l'intensité de votre douleur de 0 à 10 ?")
    private String libelle;

    @Schema(description = "Seuil d'alerte", example = "7")
    private int seuilAlerte;

    public QuestionResponse() {
    }

    public QuestionResponse(Long id, String libelle, int seuilAlerte) {
        this.id = id;
        this.libelle = libelle;
        this.seuilAlerte = seuilAlerte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
