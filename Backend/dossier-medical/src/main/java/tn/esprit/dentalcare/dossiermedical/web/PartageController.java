package tn.esprit.dentalcare.dossiermedical.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.dossiermedical.dto.DossierPartageResponse;
import tn.esprit.dentalcare.dossiermedical.dto.JournalAccesResponse;
import tn.esprit.dentalcare.dossiermedical.dto.PartageRequest;
import tn.esprit.dentalcare.dossiermedical.dto.PartageResponse;
import tn.esprit.dentalcare.dossiermedical.service.PartageService;

import java.util.List;

/**
 * Partage temporaire du dossier et journal des accès.
 * Le lien de consultation reste sous /api/dossiers/** pour passer par la route Gateway existante.
 */
@RestController
@RequestMapping("/api/dossiers")
@Tag(name = "6. Partage & journal d'accès", description = "Liens temporaires limités, chaque consultation est journalisée")
public class PartageController {

    private final PartageService partageService;

    public PartageController(PartageService partageService) {
        this.partageService = partageService;
    }

    @PostMapping("/{dossierId}/partages")
    @Operation(summary = "Créer un lien de partage temporaire",
            description = "Jeton aléatoire, durée de 1 à 72 h, portée limitée, nombre de consultations optionnel. "
                    + "Le 403 (seuls le patient et le dentiste partagent) arrivera avec Keycloak en S8.")
    @ApiResponse(responseCode = "201", description = "Lien créé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public ResponseEntity<PartageResponse> creer(@PathVariable("dossierId") Long dossierId,
                                                 @Valid @RequestBody PartageRequest requete) {
        PartageResponse cree = partageService.creer(dossierId, requete);
        return ResponseEntity.created(Locations.de(cree.id())).body(cree);
    }

    @GetMapping("/{dossierId}/partages")
    @Operation(summary = "Lister les partages du dossier (actifs et passés)")
    public List<PartageResponse> lister(@PathVariable("dossierId") Long dossierId) {
        return partageService.lister(dossierId);
    }

    @DeleteMapping("/{dossierId}/partages/{partageId}")
    @Operation(summary = "Révoquer un lien de partage", description = "Le lien cesse de fonctionner, l'historique est conservé.")
    @ApiResponse(responseCode = "204", description = "Lien révoqué")
    @ApiResponse(responseCode = "404", description = "Partage absent de ce dossier")
    public ResponseEntity<Void> revoquer(@PathVariable("dossierId") Long dossierId,
                                         @PathVariable("partageId") Long partageId) {
        partageService.revoquer(dossierId, partageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partages/{token}")
    @Operation(summary = "Ouvrir un lien de partage",
            description = "Renvoie uniquement ce que la portée autorise et inscrit l'accès au journal. "
                    + "praticienId sera lu dans le token Keycloak à partir de S8.")
    @ApiResponse(responseCode = "200", description = "Dossier partagé")
    @ApiResponse(responseCode = "403", description = "Lien destiné à un autre praticien")
    @ApiResponse(responseCode = "404", description = "Lien inconnu")
    @ApiResponse(responseCode = "410", description = "Lien expiré, révoqué ou épuisé")
    public DossierPartageResponse consulter(@PathVariable("token") String token,
                                            @RequestParam(name = "praticienId") String praticienId,
                                            HttpServletRequest requete) {
        return partageService.consulter(token, praticienId, adresseClient(requete));
    }

    @GetMapping("/{dossierId}/journal-acces")
    @Operation(summary = "Qui a consulté ce dossier ?", description = "Journal en lecture seule, du plus récent au plus ancien.")
    @ApiResponse(responseCode = "200", description = "Journal des accès")
    @ApiResponse(responseCode = "404", description = "Dossier introuvable")
    public List<JournalAccesResponse> journal(@PathVariable("dossierId") Long dossierId) {
        return partageService.journal(dossierId);
    }

    /** Derrière la Gateway, l'adresse d'origine est dans X-Forwarded-For. */
    private static String adresseClient(HttpServletRequest requete) {
        String transmise = requete.getHeader("X-Forwarded-For");
        String adresse = (transmise != null && !transmise.isBlank())
                ? transmise.split(",")[0].trim()
                : requete.getRemoteAddr();
        return adresse.length() > 64 ? adresse.substring(0, 64) : adresse;
    }
}
