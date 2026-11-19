package tn.esprit.dentalcare.plantraitement.dto;

import java.math.BigDecimal;
import tn.esprit.dentalcare.plantraitement.domain.ActeCatalogue;

public record ActeCatalogueResponse(
        String code,
        String libelle,
        Integer dureeMinutes,
        BigDecimal tarif,
        Integer delaiCicatrisationJours,
        String competenceRequise
) {
    public static ActeCatalogueResponse from(ActeCatalogue acte) {
        return new ActeCatalogueResponse(
                acte.getCode(),
                acte.getLibelle(),
                acte.getDureeMinutes(),
                acte.getTarif(),
                acte.getDelaiCicatrisationJours(),
                acte.getCompetenceRequise()
        );
    }
}
