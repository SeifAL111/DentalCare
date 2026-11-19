package tn.esprit.dentalcare.agendaclinique.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.DeplacementRequest;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.RendezVousResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.ReservationRequest;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.UrgenceRequest;
import tn.esprit.dentalcare.agendaclinique.service.RendezVousService;

@RestController
@RequestMapping("/api/rdv")
@Tag(name = "Rendez-vous", description = "Réservation sans double réservation, annulation, déplacement, urgence")
public class RendezVousController {

    private final RendezVousService service;

    public RendezVousController(RendezVousService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les rendez-vous (filtres facultatifs : patientId, praticien, date)")
    public List<RendezVousResponse> lister(
            @RequestParam(name = "patientId", required = false) String patientId,
            @RequestParam(name = "praticien", required = false) String praticienId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.lister(patientId, praticienId, date);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un rendez-vous (404 si inconnu)")
    public RendezVousResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Réserver un créneau : 201, 400 (hors disponibilité / acte inconnu / passé), "
            + "404 (fauteuil), 409 (fauteuil, praticien ou patient déjà pris)")
    public RendezVousResponse reserver(@Valid @RequestBody ReservationRequest request) {
        return service.reserver(request);
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler et libérer le créneau, offert 15 min à la liste d'attente : 200, 404, 409")
    public RendezVousResponse annuler(@PathVariable Long id) {
        return service.annuler(id);
    }

    @PatchMapping("/{id}/deplacer")
    @Operation(summary = "Déplacer : l'ancien passe à DEPLACE, renvoie le nouveau rendez-vous (409 si pris)")
    public RendezVousResponse deplacer(@PathVariable Long id, @Valid @RequestBody DeplacementRequest request) {
        return service.deplacer(id, request);
    }

    @PatchMapping("/{id}/terminer")
    @Operation(summary = "Marquer le rendez-vous TERMINE")
    public RendezVousResponse terminer(@PathVariable Long id) {
        return service.terminer(id);
    }

    @PatchMapping("/{id}/absent")
    @Operation(summary = "Marquer le patient ABSENT")
    public RendezVousResponse absent(@PathVariable Long id) {
        return service.marquerAbsent(id);
    }

    @PostMapping("/urgence")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Réserver le premier créneau d'urgence libre dans les 3 jours (complication signalée)")
    public RendezVousResponse urgence(@Valid @RequestBody UrgenceRequest request) {
        return service.reserverUrgence(request);
    }
}
