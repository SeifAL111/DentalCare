package tn.esprit.dentalcare.agendaclinique.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import tn.esprit.dentalcare.agendaclinique.domain.InscriptionAttente;
import tn.esprit.dentalcare.agendaclinique.domain.OffreCreneau;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutInscription;
import tn.esprit.dentalcare.agendaclinique.domain.StatutOffre;

public final class ListeAttenteDtos {

    private ListeAttenteDtos() {
    }

    /** praticienId facultatif : null = n'importe quel praticien. */
    public record InscriptionRequest(
            @NotBlank String patientId,
            @NotBlank String acteCode,
            String praticienId) {
    }

    public record InscriptionResponse(
            Long id,
            String patientId,
            String acteCode,
            String praticienId,
            LocalDateTime inscritLe,
            StatutInscription statut) {

        public static InscriptionResponse from(InscriptionAttente i) {
            return new InscriptionResponse(
                    i.getId(), i.getPatientId(), i.getActeCode(), i.getPraticienId(), i.getInscritLe(), i.getStatut());
        }
    }

    public record OffreResponse(
            Long id,
            Long inscriptionId,
            String patientId,
            String acteCode,
            Long rendezVousLibereId,
            String praticienId,
            Long fauteuilId,
            LocalDateTime debut,
            LocalDateTime fin,
            LocalDateTime expireLe,
            StatutOffre statut,
            Long rendezVousCreeId) {

        public static OffreResponse from(OffreCreneau o) {
            RendezVous libere = o.getRendezVousLibere();
            InscriptionAttente i = o.getInscription();
            return new OffreResponse(
                    o.getId(),
                    i.getId(),
                    i.getPatientId(),
                    i.getActeCode(),
                    libere.getId(),
                    libere.getPraticienId(),
                    libere.getFauteuil().getId(),
                    libere.getDebut(),
                    libere.getFin(),
                    o.getExpireLe(),
                    o.getStatut(),
                    o.getRendezVousCreeId());
        }
    }
}
