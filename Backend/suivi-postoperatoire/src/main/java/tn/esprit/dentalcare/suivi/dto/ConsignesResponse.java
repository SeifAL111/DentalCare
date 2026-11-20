package tn.esprit.dentalcare.suivi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Consignes médicales post-opératoires associées au suivi")
public class ConsignesResponse {

    @Schema(description = "Identifiant du suivi", example = "1")
    private Long suiviId;

    @Schema(description = "Code de l'acte dentaire", example = "DEVITALISATION")
    private String acteCode;

    @Schema(description = "Consignes spécifiques à respecter", example = "Prendre les antalgiques prescrits en cas de douleur...")
    private String consignes;

    public ConsignesResponse() {
    }

    public ConsignesResponse(Long suiviId, String acteCode, String consignes) {
        this.suiviId = suiviId;
        this.acteCode = acteCode;
        this.consignes = consignes;
    }

    public Long getSuiviId() {
        return suiviId;
    }

    public void setSuiviId(Long suiviId) {
        this.suiviId = suiviId;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public String getConsignes() {
        return consignes;
    }

    public void setConsignes(String consignes) {
        this.consignes = consignes;
    }
}
