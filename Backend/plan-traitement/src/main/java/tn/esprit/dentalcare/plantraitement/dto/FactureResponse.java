package tn.esprit.dentalcare.plantraitement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import tn.esprit.dentalcare.plantraitement.domain.Facture;

public record FactureResponse(
        Long id,
        Long planId,
        Long etapeId,
        BigDecimal montant,
        LocalDateTime emiseLe
) {
    public static FactureResponse from(Facture facture) {
        return new FactureResponse(
                facture.getId(),
                facture.getPlanId(),
                facture.getEtapeId(),
                facture.getMontant(),
                facture.getEmiseLe()
        );
    }
}
