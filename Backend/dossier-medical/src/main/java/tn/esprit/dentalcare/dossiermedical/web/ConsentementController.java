package tn.esprit.dentalcare.dossiermedical.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementRequest;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.IntegriteConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.RevocationRequest;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.service.ConsentementService;

import java.util.List;

/**
 * Pas de PUT ni de DELETE : un consentement signé est un document qui ne se modifie pas.
 */
@RestController
@RequestMapping("/api/dossiers/{dossierId}/consentements")
@Tag(name = "4. Consentements", description = "Consentements éclairés : signature, révocation, intégrité, vérification avant acte")
public class ConsentementController {

    private final ConsentementService consentementService;

    public ConsentementController(ConsentementService consentementService) {
        this.consentementService = consentementService;
    }

    @PostMapping
    @Operation(summary = "Enregistrer un consentement signé",
            description = "Calcule une empreinte SHA-256 du document et une date d'expiration.")
    @ApiResponse(responseCode = "201", description = "Consentement enregistré")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    @ApiResponse(responseCode = "409", description = "Un consentement valide existe déjà pour cet acte")
    public ResponseEntity<ConsentementResponse> enregistrer(@PathVariable("dossierId") Long dossierId,
                                                            @Valid @RequestBody ConsentementRequest requete) {
        ConsentementResponse cree = consentementService.enregistrer(dossierId, requete);
        return ResponseEntity.created(Locations.de(cree.id())).body(cree);
    }

    @GetMapping
    @Operation(summary = "Lister les consentements", description = "Filtre optionnel par type d'acte.")
    public List<ConsentementResponse> lister(@PathVariable("dossierId") Long dossierId,
                                             @RequestParam(name = "typeActe", required = false) TypeActe typeActe) {
        return consentementService.lister(dossierId, typeActe);
    }

    @GetMapping("/verifier")
    @Operation(summary = "Un acte peut-il être réalisé ? (consentement)",
            description = "Règle métier : consentement signé obligatoire avant tout acte invasif.")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification (voir le champ autorise)")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public VerificationConsentementResponse verifier(@PathVariable("dossierId") Long dossierId,
                                                     @RequestParam(name = "acte") TypeActe acte) {
        return consentementService.verifier(dossierId, acte);
    }

    @GetMapping("/{consentementId}")
    @Operation(summary = "Consulter un consentement")
    @ApiResponse(responseCode = "200", description = "Consentement trouvé")
    @ApiResponse(responseCode = "404", description = "Consentement absent de ce dossier")
    public ConsentementResponse consulter(@PathVariable("dossierId") Long dossierId,
                                          @PathVariable("consentementId") Long consentementId) {
        return consentementService.consulter(dossierId, consentementId);
    }

    @PatchMapping("/{consentementId}/revoquer")
    @Operation(summary = "Révoquer un consentement", description = "Le patient peut retirer son consentement à tout moment.")
    @ApiResponse(responseCode = "200", description = "Consentement révoqué")
    @ApiResponse(responseCode = "404", description = "Consentement absent de ce dossier")
    @ApiResponse(responseCode = "409", description = "Consentement déjà révoqué")
    public ConsentementResponse revoquer(@PathVariable("dossierId") Long dossierId,
                                         @PathVariable("consentementId") Long consentementId,
                                         @Valid @RequestBody(required = false) RevocationRequest requete) {
        return consentementService.revoquer(dossierId, consentementId, requete == null ? null : requete.motif());
    }

    @GetMapping("/{consentementId}/integrite")
    @Operation(summary = "Vérifier que le document n'a pas été modifié depuis la signature",
            description = "Recalcule l'empreinte SHA-256 et la compare à celle enregistrée.")
    @ApiResponse(responseCode = "200", description = "Résultat du contrôle (voir le champ integre)")
    @ApiResponse(responseCode = "404", description = "Consentement absent de ce dossier")
    public IntegriteConsentementResponse integrite(@PathVariable("dossierId") Long dossierId,
                                                   @PathVariable("consentementId") Long consentementId) {
        return consentementService.verifierIntegrite(dossierId, consentementId);
    }
}
