package tn.esprit.dentalcare.agendaclinique.service;

import java.time.LocalDateTime;

/** Demande interne de réservation (réservation directe, déplacement, urgence ou offre acceptée). */
public record DemandeReservation(
        String patientId,
        String praticienId,
        Long fauteuilId,
        String acteCode,
        LocalDateTime debut,
        boolean urgence,
        Long etapePlanId,
        String motif) {
}
