package tn.esprit.dentalcare.dossiermedical.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.dto.FacteursRisqueResponse;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationRequest;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationResponse;
import tn.esprit.dentalcare.dossiermedical.service.ConsentementService;
import tn.esprit.dentalcare.dossiermedical.service.RisqueMedicalService;

/**
 * Endpoints appelés par les autres microservices (OpenFeign en S6).
 * Chacun existe en deux versions : par id de dossier, et par patientId,
 * car l'Odontogramme et le Suivi ne connaissent que le patientId.
 */
@RestController
@RequestMapping("/api/dossiers")
@Tag(name = "5. Contrôles médicaux", description = "Contre-indications, facteurs de risque, consentement avant acte")
public class RisqueMedicalController {

    private final RisqueMedicalService risqueMedicalService;
    private final ConsentementService consentementService;

    public RisqueMedicalController(RisqueMedicalService risqueMedicalService,
                                   ConsentementService consentementService) {
        this.risqueMedicalService = risqueMedicalService;
        this.consentementService = consentementService;
    }

    @PostMapping("/{dossierId}/contre-indications/verifier")
    @Operation(summary = "Contrôler une prescription",
            description = "Verdict OK, AVERTISSEMENT ou BLOQUANT avec les raisons en clair. "
                    + "Familles de molécules, réactions croisées, interactions avec les traitements en cours.")
    @ApiResponse(responseCode = "200", description = "Verdict rendu (voir le champ niveau)")
    @ApiResponse(responseCode = "400", description = "Médicament manquant")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public VerificationResponse verifierPrescription(@PathVariable("dossierId") Long dossierId,
                                                     @Valid @RequestBody VerificationRequest requete) {
        return risqueMedicalService.verifierPrescription(dossierId, requete.medicament());
    }

    @PostMapping("/patient/{patientId}/contre-indications/verifier")
    @Operation(summary = "Contrôler une prescription (par patientId)", description = "Pour l'Odontogramme & Actes.")
    @ApiResponse(responseCode = "200", description = "Verdict rendu (voir le champ niveau)")
    @ApiResponse(responseCode = "404", description = "Aucun dossier pour ce patient")
    public VerificationResponse verifierPrescriptionParPatient(@PathVariable("patientId") String patientId,
                                                               @Valid @RequestBody VerificationRequest requete) {
        return risqueMedicalService.verifierPrescriptionParPatient(patientId, requete.medicament());
    }

    @GetMapping("/{dossierId}/facteurs-risque")
    @Operation(summary = "Facteurs de risque du patient",
            description = "Avec acte : seuls les facteurs qui concernent cet acte. "
                    + "protocoleRenforce indique au Suivi Post-opératoire d'appliquer le protocole renforcé.")
    @ApiResponse(responseCode = "200", description = "Évaluation rendue")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public FacteursRisqueResponse facteursRisque(@PathVariable("dossierId") Long dossierId,
                                                 @RequestParam(name = "acte", required = false) TypeActe acte) {
        return risqueMedicalService.facteursRisque(dossierId, acte);
    }

    @GetMapping("/patient/{patientId}/facteurs-risque")
    @Operation(summary = "Facteurs de risque (par patientId)", description = "Pour le Suivi Post-opératoire.")
    @ApiResponse(responseCode = "200", description = "Évaluation rendue")
    @ApiResponse(responseCode = "404", description = "Aucun dossier pour ce patient")
    public FacteursRisqueResponse facteursRisqueParPatient(@PathVariable("patientId") String patientId,
                                                           @RequestParam(name = "acte", required = false) TypeActe acte) {
        return risqueMedicalService.facteursRisqueParPatient(patientId, acte);
    }

    @GetMapping("/patient/{patientId}/consentements/verifier")
    @Operation(summary = "Un acte peut-il être réalisé ? (par patientId)",
            description = "Consentement signé obligatoire avant tout acte invasif.")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification (voir le champ autorise)")
    @ApiResponse(responseCode = "404", description = "Aucun dossier pour ce patient")
    public VerificationConsentementResponse verifierConsentementParPatient(
            @PathVariable("patientId") String patientId,
            @RequestParam(name = "acte") TypeActe acte) {
        return consentementService.verifierParPatient(patientId, acte);
    }
}
