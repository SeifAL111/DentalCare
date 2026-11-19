package tn.esprit.dentalcare.dossiermedical.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.dossiermedical.dto.DossierCreateRequest;
import tn.esprit.dentalcare.dossiermedical.dto.DossierDetailResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierUpdateRequest;
import tn.esprit.dentalcare.dossiermedical.dto.PageResponse;
import tn.esprit.dentalcare.dossiermedical.service.DossierService;

@RestController
@RequestMapping("/api/dossiers")
@Tag(name = "1. Dossiers médicaux", description = "Un dossier par patient : création, consultation, modification, archivage")
public class DossierController {

    private final DossierService dossierService;

    public DossierController(DossierService dossierService) {
        this.dossierService = dossierService;
    }

    @PostMapping
    @Operation(summary = "Créer un dossier médical")
    @ApiResponse(responseCode = "201", description = "Dossier créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Le patient possède déjà un dossier")
    public ResponseEntity<DossierResponse> creer(@Valid @RequestBody DossierCreateRequest requete) {
        DossierResponse cree = dossierService.creer(requete);
        return ResponseEntity.created(Locations.de(cree.id())).body(cree);
    }

    @GetMapping
    @Operation(summary = "Lister les dossiers (paginé)",
            description = "recherche filtre sur l'identifiant patient ; taille limitée à 100.")
    public PageResponse<DossierResponse> lister(
            @RequestParam(name = "recherche", required = false) String recherche,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "taille", defaultValue = "20") int taille) {
        return dossierService.lister(recherche, page, taille);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un dossier complet (allergies, antécédents, consentements)")
    @ApiResponse(responseCode = "200", description = "Dossier trouvé")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable ou archivé")
    public DossierDetailResponse consulter(@PathVariable("id") Long id) {
        return dossierService.consulter(id);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Consulter le dossier d'un patient par son identifiant",
            description = "Les autres microservices ne stockent que le patientId : c'est leur point d'entrée.")
    @ApiResponse(responseCode = "200", description = "Dossier trouvé")
    @ApiResponse(responseCode = "404", description = "Aucun dossier pour ce patient")
    public DossierDetailResponse consulterParPatient(@PathVariable("patientId") String patientId) {
        return dossierService.consulterParPatient(patientId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier les informations générales d'un dossier",
            description = "Renvoyer la version lue : si le dossier a changé entre-temps, la modification est refusée.")
    @ApiResponse(responseCode = "200", description = "Dossier modifié")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    @ApiResponse(responseCode = "409", description = "Version obsolète (modification concurrente)")
    public DossierResponse modifier(@PathVariable("id") Long id, @Valid @RequestBody DossierUpdateRequest requete) {
        return dossierService.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archiver un dossier",
            description = "Un dossier médical ne se supprime pas : il est archivé et n'apparaît plus.")
    @ApiResponse(responseCode = "204", description = "Dossier archivé")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public ResponseEntity<Void> archiver(@PathVariable("id") Long id) {
        dossierService.archiver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/declarer-aucune-allergie")
    @Operation(summary = "Déclarer « aucune allergie connue »",
            description = "Le praticien confirme avoir interrogé le patient. Sans cette déclaration, "
                    + "un dossier sans allergie reste « non renseigné » et chaque prescription donne un avertissement.")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour")
    @ApiResponse(responseCode = "409", description = "Le dossier contient déjà des allergies")
    public DossierResponse declarerAucuneAllergie(@PathVariable("id") Long id) {
        return dossierService.declarerAucuneAllergie(id);
    }
}
