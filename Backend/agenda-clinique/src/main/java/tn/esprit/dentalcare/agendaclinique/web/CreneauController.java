package tn.esprit.dentalcare.agendaclinique.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.CreneauResponse;
import tn.esprit.dentalcare.agendaclinique.service.CreneauService;

@RestController
@RequestMapping("/api/creneaux")
@Tag(name = "Créneaux", description = "Recherche de créneaux compatibles avec un acte")
public class CreneauController {

    private final CreneauService service;

    public CreneauController(CreneauService service) {
        this.service = service;
    }

    /** GET /api/creneaux?acte=DEVIT&praticien=PRAT-001&date=2026-11-23&jours=7 — 200, 400. */
    @GetMapping
    @Operation(summary = "Créneaux compatibles : praticien disponible et libre, fauteuil libre, durée de l'acte "
            + "(400 si acte inconnu)")
    public List<CreneauResponse> rechercher(
            @RequestParam("acte") String acteCode,
            @RequestParam(name = "praticien", required = false) String praticienId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(name = "jours", defaultValue = "7") int jours) {
        return service.rechercher(acteCode, praticienId, date, jours);
    }
}
