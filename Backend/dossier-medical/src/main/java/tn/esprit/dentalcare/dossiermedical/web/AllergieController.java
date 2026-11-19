package tn.esprit.dentalcare.dossiermedical.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieRequest;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieResponse;
import tn.esprit.dentalcare.dossiermedical.service.AllergieService;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers/{dossierId}/allergies")
@Tag(name = "2. Allergies", description = "Allergies aux médicaments, matériaux dentaires, aliments")
public class AllergieController {

    private final AllergieService allergieService;

    public AllergieController(AllergieService allergieService) {
        this.allergieService = allergieService;
    }

    @PostMapping
    @Operation(summary = "Ajouter une allergie",
            description = "La réponse indique les familles de médicaments reconnues (ex. Pénicilline → PENICILLINES).")
    @ApiResponse(responseCode = "201", description = "Allergie ajoutée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public ResponseEntity<AllergieResponse> ajouter(@PathVariable("dossierId") Long dossierId,
                                                    @Valid @RequestBody AllergieRequest requete) {
        AllergieResponse creee = allergieService.ajouter(dossierId, requete);
        return ResponseEntity.created(Locations.de(creee.id())).body(creee);
    }

    @GetMapping
    @Operation(summary = "Lister les allergies du dossier")
    public List<AllergieResponse> lister(@PathVariable("dossierId") Long dossierId) {
        return allergieService.lister(dossierId);
    }

    @GetMapping("/{allergieId}")
    @Operation(summary = "Consulter une allergie")
    @ApiResponse(responseCode = "200", description = "Allergie trouvée")
    @ApiResponse(responseCode = "404", description = "Allergie absente de ce dossier")
    public AllergieResponse consulter(@PathVariable("dossierId") Long dossierId,
                                      @PathVariable("allergieId") Long allergieId) {
        return allergieService.consulter(dossierId, allergieId);
    }

    @PutMapping("/{allergieId}")
    @Operation(summary = "Modifier une allergie")
    @ApiResponse(responseCode = "200", description = "Allergie modifiée")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Allergie absente de ce dossier")
    public AllergieResponse modifier(@PathVariable("dossierId") Long dossierId,
                                     @PathVariable("allergieId") Long allergieId,
                                     @Valid @RequestBody AllergieRequest requete) {
        return allergieService.modifier(dossierId, allergieId, requete);
    }

    @DeleteMapping("/{allergieId}")
    @Operation(summary = "Supprimer une allergie (erreur de saisie)",
            description = "Si c'était la dernière, le statut des allergies redevient NON_RENSEIGNE.")
    @ApiResponse(responseCode = "204", description = "Allergie supprimée")
    @ApiResponse(responseCode = "404", description = "Allergie absente de ce dossier")
    public ResponseEntity<Void> supprimer(@PathVariable("dossierId") Long dossierId,
                                          @PathVariable("allergieId") Long allergieId) {
        allergieService.supprimer(dossierId, allergieId);
        return ResponseEntity.noContent().build();
    }
}
