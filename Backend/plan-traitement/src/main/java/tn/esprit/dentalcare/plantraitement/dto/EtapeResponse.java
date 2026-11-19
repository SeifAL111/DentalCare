package tn.esprit.dentalcare.plantraitement.dto;

import tn.esprit.dentalcare.plantraitement.domain.EtapePlan;
import tn.esprit.dentalcare.plantraitement.domain.StatutEtape;

public record EtapeResponse(
        Long id,
        Integer ordre,
        String acteCode,
        Integer dent,
        StatutEtape statut
) {
    public static EtapeResponse from(EtapePlan etape) {
        return new EtapeResponse(
                etape.getId(),
                etape.getOrdre(),
                etape.getActeCode(),
                etape.getDent(),
                etape.getStatut()
        );
    }
}
