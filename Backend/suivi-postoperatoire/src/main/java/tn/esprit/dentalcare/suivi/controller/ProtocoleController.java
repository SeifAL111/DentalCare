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
import tn.esprit.dentalcare.suivi.dto.ProtocoleRequest;
import tn.esprit.dentalcare.suivi.dto.ProtocoleResponse;
import tn.esprit.dentalcare.suivi.exception.ErrorResponse;
import tn.esprit.dentalcare.suivi.service.ProtocoleService;

import java.util.List;

@RestController
@RequestMapping("/api/protocoles")
@Tag(name = "Protocoles", description = "Endpoints de gestion des protocoles de suivi post-opératoire")
public class ProtocoleController {

    private final ProtocoleService protocoleService;

    public ProtocoleController(ProtocoleService protocoleService) {
        this.protocoleService = protocoleService;
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau protocole de suivi avec ses questions",
            description = "Enregistre un protocole rattaché à un code d'acte dentaire unique avec sa liste de jours et ses questions de suivi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Protocole créé avec succès",
                    content = @Content(schema = @Schema(implementation = ProtocoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides dans la requête",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Un protocole existe déjà pour cet acte",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProtocoleResponse> createProtocole(@Valid @RequestBody ProtocoleRequest request) {
        ProtocoleResponse response = protocoleService.createProtocole(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister tous les protocoles post-opératoires")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des protocoles récupérée avec succès")
    })
    public ResponseEntity<List<ProtocoleResponse>> getAllProtocoles() {
        return ResponseEntity.ok(protocoleService.getAllProtocoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un protocole par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Protocole trouvé"),
            @ApiResponse(responseCode = "404", description = "Protocole introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProtocoleResponse> getProtocoleById(@PathVariable Long id) {
        return ResponseEntity.ok(protocoleService.getProtocoleById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un protocole post-opératoire",
            description = "Supprime un protocole et ses questions associées (opération CRUD DELETE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Protocole supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Protocole introuvable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProtocole(@PathVariable Long id) {
        protocoleService.deleteProtocole(id);
        return ResponseEntity.noContent().build();
    }
}
