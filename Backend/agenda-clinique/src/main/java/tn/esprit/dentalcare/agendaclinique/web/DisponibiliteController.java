package tn.esprit.dentalcare.agendaclinique.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.dto.DisponibiliteDtos.DisponibiliteRequest;
import tn.esprit.dentalcare.agendaclinique.dto.DisponibiliteDtos.DisponibiliteResponse;
import tn.esprit.dentalcare.agendaclinique.service.DisponibiliteService;

@RestController
@RequestMapping("/api/disponibilites")
@Tag(name = "Disponibilités", description = "Horaires hebdomadaires des praticiens")
public class DisponibiliteController {

    private final DisponibiliteService service;

    public DisponibiliteController(DisponibiliteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les plages horaires (filtre facultatif : praticien)")
    public List<DisponibiliteResponse> lister(@RequestParam(name = "praticien", required = false) String praticienId) {
        return service.lister(praticienId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ajouter une plage horaire (400 si début >= fin, 409 si chevauchement)")
    public DisponibiliteResponse creer(@Valid @RequestBody DisponibiliteRequest request) {
        return service.creer(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une plage horaire")
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}
