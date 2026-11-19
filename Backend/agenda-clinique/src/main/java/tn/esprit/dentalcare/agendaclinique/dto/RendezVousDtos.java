package tn.esprit.dentalcare.agendaclinique.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;

public final class RendezVousDtos {

    private RendezVousDtos() {
    }

    /** Exemple : {"patientId":"PAT-AHMED","praticienId":"PRAT-001","fauteuilId":1,"acteCode":"DEVIT","debut":"2026-11-23T09:00:00"} */
    public record ReservationRequest(
            @NotBlank String patientId,
            @NotBlank String praticienId,
            @NotNull Long fauteuilId,
            @NotBlank String acteCode,
            @NotNull @Future LocalDateTime debut,
            Long etapePlanId,
            @Size(max = 255) String motif) {
    }

    /** fauteuilId facultatif : null = garder le même fauteuil. */
    public record DeplacementRequest(
            @NotNull @Future LocalDateTime nouveauDebut,
            Long fauteuilId) {
    }

    /** acteCode facultatif (URGENCE par défaut), praticienId facultatif (n'importe lequel). */
    public record UrgenceRequest(
            @NotBlank String patientId,
            String acteCode,
            String praticienId,
            @Size(max = 255) String motif) {
    }

    public record RendezVousResponse(
            Long id,
            String patientId,
            String praticienId,
            Long fauteuilId,
            String fauteuilNumero,
            String acteCode,
            LocalDateTime debut,
            LocalDateTime fin,
            int dureeMinutes,
            StatutRdv statut,
            boolean urgence,
            Long etapePlanId,
            String motif,
            LocalDateTime creeLe) {

        public static RendezVousResponse from(RendezVous r) {
            return new RendezVousResponse(
                    r.getId(),
                    r.getPatientId(),
                    r.getPraticienId(),
                    r.getFauteuil().getId(),
                    r.getFauteuil().getNumero(),
                    r.getActeCode(),
                    r.getDebut(),
                    r.getFin(),
                    r.getDureeMinutes(),
                    r.getStatut(),
                    r.isUrgence(),
                    r.getEtapePlanId(),
                    r.getMotif(),
                    r.getCreeLe());
        }
    }

    public record CreneauResponse(
            String praticienId,
            Long fauteuilId,
            String fauteuilNumero,
            String salle,
            String acteCode,
            LocalDateTime debut,
            LocalDateTime fin,
            int dureeMinutes) {
    }
}
