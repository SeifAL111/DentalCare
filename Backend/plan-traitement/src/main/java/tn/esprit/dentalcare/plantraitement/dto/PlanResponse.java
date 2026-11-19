package tn.esprit.dentalcare.plantraitement.dto;

import java.time.LocalDateTime;
import java.util.List;
import tn.esprit.dentalcare.plantraitement.domain.PlanTraitement;
import tn.esprit.dentalcare.plantraitement.domain.StatutPlan;

public record PlanResponse(
        Long id,
        String patientId,
        StatutPlan statut,
        LocalDateTime creeLe,
        LocalDateTime misAJourLe,
        List<EtapeResponse> etapes,
        DevisResponse devis
) {
    public static PlanResponse from(PlanTraitement plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getPatientId(),
                plan.getStatut(),
                plan.getCreeLe(),
                plan.getMisAJourLe(),
                plan.getEtapes().stream().map(EtapeResponse::from).toList(),
                plan.getDevis() == null ? null : DevisResponse.from(plan.getDevis())
        );
    }
}
