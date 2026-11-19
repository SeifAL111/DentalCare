package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Vue complète d'un dossier, renvoyée par GET /api/dossiers/{id}.
 */
public record DossierDetailResponse(
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
        Long version,
        List<AllergieResponse> allergies,
        List<AntecedentResponse> antecedents,
        List<ConsentementResponse> consentements) {

    public static DossierDetailResponse from(DossierMedical d,
                                             List<AllergieResponse> allergies,
                                             List<AntecedentResponse> antecedents,
                                             List<ConsentementResponse> consentements) {
        return new DossierDetailResponse(
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
                d.getVersion(),
                allergies,
                antecedents,
                consentements);
    }
}
