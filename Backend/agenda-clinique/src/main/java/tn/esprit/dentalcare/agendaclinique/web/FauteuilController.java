package tn.esprit.dentalcare.agendaclinique.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.dto.FauteuilDtos.FauteuilRequest;
import tn.esprit.dentalcare.agendaclinique.dto.FauteuilDtos.FauteuilResponse;
import tn.esprit.dentalcare.agendaclinique.service.FauteuilService;

@RestController
@RequestMapping("/api/fauteuils")
@Tag(name = "Fauteuils", description = "Fauteuils dentaires et salles")
public class FauteuilController {

    private final FauteuilService service;

    public FauteuilController(FauteuilService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les fauteuils")
    public List<FauteuilResponse> lister() {
        return service.lister();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un fauteuil (404 si inconnu)")
    public FauteuilResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un fauteuil (409 si le numéro existe déjà)")
    public FauteuilResponse creer(@Valid @RequestBody FauteuilRequest request) {
        return service.creer(request);
    }

    @PatchMapping("/{id}/hors-service")
    @Operation(summary = "Mettre un fauteuil hors service (plus de nouvelles réservations)")
    public FauteuilResponse horsService(@PathVariable Long id) {
        return service.changerEtat(id, false);
    }

    @PatchMapping("/{id}/en-service")
    @Operation(summary = "Remettre un fauteuil en service")
    public FauteuilResponse enService(@PathVariable Long id) {
        return service.changerEtat(id, true);
    }
}
