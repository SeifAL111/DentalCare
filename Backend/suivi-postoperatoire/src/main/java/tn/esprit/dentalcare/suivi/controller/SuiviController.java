package tn.esprit.dentalcare.suivi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.dentalcare.suivi.dto.*;
import tn.esprit.dentalcare.suivi.exception.ErrorResponse;
import tn.esprit.dentalcare.suivi.service.ReponseService;
import tn.esprit.dentalcare.suivi.service.SuiviService;

import java.util.List;

@RestController
@RequestMapping("/api/suivis")
@Tag(name = "Suivis Post-opératoires", description = "Endpoints de suivi des patients, réponses, alertes et clôture")
public class SuiviController {

    private final SuiviService suiviService;
    private final ReponseService reponseService;

    public SuiviController(SuiviService suiviService, ReponseService reponseService) {
        this.suiviService = suiviService;
        this.reponseService = reponseService;
    }

    @PostMapping
    @Operation(summary = "Ouvrir un nouveau suivi post-opératoire",
            description = "Ouvre un suivi avec le statut initial ACTIF pour un patient et un acte donnés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Suivi ouvert avec succès",
                    content = @Content(schema = @Schema(implementation = SuiviResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Protocole introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuiviResponse> openSuivi(@Valid @RequestBody SuiviRequest request) {
        SuiviResponse response = suiviService.openSuivi(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/alertes")
    @Operation(summary = "Lister les suivis au statut EN_ALERTE",
            description = "Récupère tous les suivis en alerte pour le tableau de bord de l'assistant médical.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des suivis en alerte récupérée avec succès")
    })
    public ResponseEntity<List<SuiviResponse>> getSuivisEnAlerte() {
        return ResponseEntity.ok(suiviService.getSuivisEnAlerte());
    }

    @GetMapping("/patient/{id}")
    @Operation(summary = "Lister les suivis d'un patient",
            description = "Récupère l'historique des suivis post-opératoires rattachés à un identifiant patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suivis du patient trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun suivi trouvé pour ce patient",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<SuiviResponse>> getSuivisByPatient(@PathVariable("id") String patientId) {
        return ResponseEntity.ok(suiviService.getSuivisByPatient(patientId));
    }

    @GetMapping("/{id:[0-9]+}/consignes")
    @Operation(summary = "Consulter les consignes post-opératoires d'un suivi",
            description = "Renvoie les consignes médicales définies dans le protocole associé à ce suivi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consignes récupérées"),
            @ApiResponse(responseCode = "404", description = "Suivi introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ConsignesResponse> getConsignes(@PathVariable("id") Long id) {
        return ResponseEntity.ok(suiviService.getConsignes(id));
    }

    @PostMapping("/{id:[0-9]+}/reponses")
    @Operation(summary = "Enregistrer une réponse du patient",
            description = "Enregistre la réponse à une question pour un jour J+. Si valeur >= seuilAlerte, crée une Alerte et passe le suivi à EN_ALERTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réponse enregistrée avec succès",
                    content = @Content(schema = @Schema(implementation = ReponseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (jour hors protocole, valeur hors 0-10, question non liée)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Suivi ou question introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflit : suivi clôturé ou doublon de réponse",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReponseResponse> enregistrerReponse(@PathVariable("id") Long id,
                                                              @Valid @RequestBody ReponseRequest request) {
        ReponseResponse response = reponseService.enregistrerReponse(id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id:[0-9]+}/prendre-en-charge")
    @Operation(summary = "Prendre en charge un suivi en alerte",
            description = "Fait passer un suivi du statut EN_ALERTE à PRIS_EN_CHARGE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suivi pris en charge avec succès",
                    content = @Content(schema = @Schema(implementation = SuiviResponse.class))),
            @ApiResponse(responseCode = "404", description = "Suivi introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transition invalide (le suivi n'est pas au statut EN_ALERTE)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuiviResponse> prendreEnCharge(@PathVariable("id") Long id) {
        return ResponseEntity.ok(suiviService.prendreEnCharge(id));
    }

    @PatchMapping("/{id:[0-9]+}/cloturer")
    @Operation(summary = "Clôturer définitivement un suivi",
            description = "Fait passer le suivi au statut CLOTURE. Action réservée au dentiste.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suivi clôturé avec succès",
                    content = @Content(schema = @Schema(implementation = SuiviResponse.class))),
            @ApiResponse(responseCode = "404", description = "Suivi introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transition invalide (le suivi est déjà clôturé)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuiviResponse> cloturer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(suiviService.cloturer(id));
    }

    @GetMapping("/{id:[0-9]+}")
    @Operation(summary = "Consulter le détail d'un suivi par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suivi trouvé"),
            @ApiResponse(responseCode = "404", description = "Suivi introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SuiviResponse> getSuiviById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(suiviService.getSuiviById(id));
    }

    @DeleteMapping("/{id:[0-9]+}")
    @Operation(summary = "Supprimer un suivi post-opératoire",
            description = "Supprime physiquement le suivi ainsi que ses réponses et alertes associées (opération CRUD DELETE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Suivi supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Suivi introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteSuivi(@PathVariable("id") Long id) {
        suiviService.deleteSuivi(id);
        return ResponseEntity.noContent().build();
    }
}
