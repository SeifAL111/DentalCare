package tn.esprit.dentalcare.plantraitement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import tn.esprit.dentalcare.plantraitement.domain.Devis;

public record DevisResponse(
        Long id,
        Long planId,
        BigDecimal montantTotal,
        LocalDate accepteLe
) {
    public static DevisResponse from(Devis devis) {
        return new DevisResponse(
                devis.getId(),
                devis.getPlan().getId(),
                devis.getMontantTotal(),
                devis.getAccepteLe()
        );
    }
}
