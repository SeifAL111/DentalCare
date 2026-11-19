package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.FamilleMedicament;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;
import tn.esprit.dentalcare.dossiermedical.service.RaisonContreIndication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Verdict renvoyé à l'Odontogramme (et plus tard à l'agent Clinique IA).
 * Le service appelant décide : BLOQUANT → il refuse l'ordonnance (422).
 */
public record VerificationResponse(
        Long dossierId,
        String patientId,
        String medicament,
        NiveauContreIndication niveau,
        Set<FamilleMedicament> famillesDetectees,
        List<RaisonContreIndication> raisons,
        boolean aValiderParPraticien,
        LocalDateTime verifieLe) {
}
