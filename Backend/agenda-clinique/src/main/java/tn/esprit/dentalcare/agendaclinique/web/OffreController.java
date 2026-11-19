package tn.esprit.dentalcare.agendaclinique.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.domain.StatutOffre;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.OffreResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.RendezVousResponse;
import tn.esprit.dentalcare.agendaclinique.service.ListeAttenteService;

@RestController
@RequestMapping("/api/offres")
@Tag(name = "Offres de créneau", description = "Créneau libéré offert 15 minutes au patient en attente")
public class OffreController {

    private final ListeAttenteService service;

    public OffreController(ListeAttenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les offres (filtres facultatifs : patientId, statut)")
    public List<OffreResponse> lister(
            @RequestParam(name = "patientId", required = false) String patientId,
            @RequestParam(name = "statut", required = false) StatutOffre statut) {
        return service.listerOffres(patientId, statut);
    }

    @PostMapping("/{id}/accepter")
    @Operation(summary = "Accepter l'offre : 200 (rendez-vous créé), 404, 409 (créneau repris), 410 (offre expirée)")
    public RendezVousResponse accepter(@PathVariable Long id) {
        return service.accepter(id);
    }

    @PostMapping("/{id}/refuser")
    @Operation(summary = "Refuser l'offre : le créneau passe au patient suivant")
    public OffreResponse refuser(@PathVariable Long id) {
        return service.refuser(id);
    }

    @PostMapping("/verifier-expirations")
    @Operation(summary = "Forcer tout de suite la vérification des offres expirées (sinon automatique chaque minute)")
    public Map<String, Integer> verifierExpirations() {
        return Map.of("offresExpirees", service.expirerOffresEchues());
    }
}
