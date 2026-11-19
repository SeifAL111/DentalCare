package tn.esprit.dentalcare.plantraitement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import tn.esprit.dentalcare.plantraitement.domain.Echeance;

public record EcheanceResponse(
        Long id,
        BigDecimal montant,
        LocalDate dateLimite,
        boolean reglee
) {
    public static EcheanceResponse from(Echeance echeance) {
        return new EcheanceResponse(
                echeance.getId(),
                echeance.getMontant(),
                echeance.getDateLimite(),
                echeance.isReglee()
        );
    }
}
