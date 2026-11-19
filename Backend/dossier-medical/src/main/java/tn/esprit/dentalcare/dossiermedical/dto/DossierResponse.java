package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DossierResponse(
        Long id,
        String patientId,
        LocalDate creeLe,
        LocalDateTime misAJourLe,
        StatutAllergies statutAllergies,
        String groupeSanguin,
        String medecinTraitant,
        String contactUrgenceNom,
        String contactUrgenceTelephone,
        String observations,
        Long version) {

    public static DossierResponse from(DossierMedical d) {
        return new DossierResponse(
                d.getId(),
                d.getPatientId(),
                d.getCreeLe(),
                d.getMisAJourLe(),
                d.getStatutAllergies(),
                d.getGroupeSanguin(),
                d.getMedecinTraitant(),
                d.getContactUrgenceNom(),
                d.getContactUrgenceTelephone(),
                d.getObservations(),
                d.getVersion());
    }
}
