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
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentRequest;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentResponse;
import tn.esprit.dentalcare.dossiermedical.service.AntecedentService;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers/{dossierId}/antecedents")
@Tag(name = "3. Antécédents", description = "Antécédents médicaux, chirurgicaux, familiaux, habitudes et traitements en cours")
public class AntecedentController {

    private final AntecedentService antecedentService;

    public AntecedentController(AntecedentService antecedentService) {
        this.antecedentService = antecedentService;
    }

    @PostMapping
    @Operation(summary = "Ajouter un antécédent",
            description = "Renseigner facteurRisque (ANTICOAGULANT, TABAC…) rend l'antécédent exploitable par les règles de risque.")
    @ApiResponse(responseCode = "201", description = "Antécédent ajouté")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public ResponseEntity<AntecedentResponse> ajouter(@PathVariable("dossierId") Long dossierId,
                                                      @Valid @RequestBody AntecedentRequest requete) {
        AntecedentResponse cree = antecedentService.ajouter(dossierId, requete);
        return ResponseEntity.created(Locations.de(cree.id())).body(cree);
    }

    @GetMapping
    @Operation(summary = "Lister les antécédents du dossier")
    public List<AntecedentResponse> lister(@PathVariable("dossierId") Long dossierId) {
        return antecedentService.lister(dossierId);
    }

    @GetMapping("/{antecedentId}")
    @Operation(summary = "Consulter un antécédent")
    @ApiResponse(responseCode = "200", description = "Antécédent trouvé")
    @ApiResponse(responseCode = "404", description = "Antécédent absent de ce dossier")
    public AntecedentResponse consulter(@PathVariable("dossierId") Long dossierId,
                                        @PathVariable("antecedentId") Long antecedentId) {
        return antecedentService.consulter(dossierId, antecedentId);
    }

    @PutMapping("/{antecedentId}")
    @Operation(summary = "Modifier un antécédent", description = "Passer actif à false pour un traitement arrêté.")
    @ApiResponse(responseCode = "200", description = "Antécédent modifié")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Antécédent absent de ce dossier")
    public AntecedentResponse modifier(@PathVariable("dossierId") Long dossierId,
                                       @PathVariable("antecedentId") Long antecedentId,
                                       @Valid @RequestBody AntecedentRequest requete) {
        return antecedentService.modifier(dossierId, antecedentId, requete);
    }

    @DeleteMapping("/{antecedentId}")
    @Operation(summary = "Supprimer un antécédent (erreur de saisie)")
    @ApiResponse(responseCode = "204", description = "Antécédent supprimé")
    @ApiResponse(responseCode = "404", description = "Antécédent absent de ce dossier")
    public ResponseEntity<Void> supprimer(@PathVariable("dossierId") Long dossierId,
                                          @PathVariable("antecedentId") Long antecedentId) {
        antecedentService.supprimer(dossierId, antecedentId);
        return ResponseEntity.noContent().build();
    }
}
