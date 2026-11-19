package tn.esprit.dentalcare.plantraitement.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.plantraitement.dto.CreatePlanRequest;
import tn.esprit.dentalcare.plantraitement.dto.DevisResponse;
import tn.esprit.dentalcare.plantraitement.dto.EcheanceResponse;
import tn.esprit.dentalcare.plantraitement.dto.FactureResponse;
import tn.esprit.dentalcare.plantraitement.dto.PlanResponse;
import tn.esprit.dentalcare.plantraitement.service.PlanTraitementService;

@RestController
@RequestMapping("/api/plans")
@Tag(name = "Plans de traitement")
public class PlanTraitementController {

    private final PlanTraitementService service;

    public PlanTraitementController(PlanTraitementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a draft treatment plan")
    public PlanResponse create(@Valid @RequestBody CreatePlanRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "List plans (optionally filter by patientId)")
    public List<PlanResponse> findAll(@RequestParam(required = false) String patientId) {
        if (patientId != null && !patientId.isBlank()) {
            return service.findByPatient(patientId);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plan by id")
    public PlanResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/proposer")
    @Operation(summary = "Move plan BROUILLON → PROPOSE")
    public PlanResponse proposer(@PathVariable Long id) {
        return service.proposer(id);
    }

    @PatchMapping("/{id}/accepter")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Accept quotation (PROPOSE → ACCEPTE). Saga will be added in S7.")
    public PlanResponse accepter(@PathVariable Long id) {
        return service.accepter(id);
    }

    @PatchMapping("/{id}/demarrer")
    @Operation(summary = "Start plan (ACCEPTE → EN_COURS)")
    public PlanResponse demarrer(@PathVariable Long id) {
        return service.demarrer(id);
    }

    @PatchMapping("/{id}/abandonner")
    @Operation(summary = "Abandon plan")
    public PlanResponse abandonner(@PathVariable Long id) {
        return service.abandonner(id);
    }

    @PostMapping("/{id}/etapes/{etapeId}/realiser")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Mark step as done and issue invoice")
    public FactureResponse realiserEtape(@PathVariable Long id, @PathVariable Long etapeId) {
        return service.realiserEtape(id, etapeId);
    }

    @GetMapping("/{id}/devis")
    @Operation(summary = "Get quotation for plan")
    public DevisResponse devis(@PathVariable Long id) {
        return service.getDevis(id);
    }

    @GetMapping("/{id}/echeancier")
    @Operation(summary = "Get payment schedule for plan")
    public List<EcheanceResponse> echeancier(@PathVariable Long id) {
        return service.getEcheancier(id);
    }

    @GetMapping("/{id}/factures")
    @Operation(summary = "List invoices issued for plan")
    public List<FactureResponse> factures(@PathVariable Long id) {
        return service.getFactures(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete draft plan only")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
