package tn.esprit.dentalcare.dossiermedical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tn.esprit.dentalcare.dossiermedical.domain.PorteePartage;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ce que voit le destinataire d'un lien de partage. Les champs hors de la portée
 * sont laissés à null et n'apparaissent pas dans le JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DossierPartageResponse(
        String patientId,
        PorteePartage portee,
        LocalDateTime lienExpireLe,
        Integer consultationsRestantes,
        StatutAllergies statutAllergies,
        List<AllergieResponse> allergies,
        List<AntecedentResponse> antecedents,
        FacteursRisqueResponse facteursRisque,
        String groupeSanguin,
        String contactUrgenceNom,
        String contactUrgenceTelephone,
        List<ConsentementResponse> consentements) {
}
