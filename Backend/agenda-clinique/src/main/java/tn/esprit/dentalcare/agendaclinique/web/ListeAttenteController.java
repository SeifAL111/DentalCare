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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.InscriptionRequest;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.InscriptionResponse;
import tn.esprit.dentalcare.agendaclinique.service.ListeAttenteService;

@RestController
@RequestMapping("/api/liste-attente")
@Tag(name = "Liste d'attente", description = "Premier inscrit, premier servi")
public class ListeAttenteController {

    private final ListeAttenteService service;

    public ListeAttenteController(ListeAttenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Patients en attente, dans l'ordre d'inscription")
    public List<InscriptionResponse> lister() {
        return service.listerEnAttente();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "S'inscrire en liste d'attente : 201, 400 (acte inconnu), 409 (déjà inscrit)")
    public InscriptionResponse inscrire(@Valid @RequestBody InscriptionRequest request) {
        return service.inscrire(request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Se retirer de la liste d'attente (l'offre en cours passe au suivant)")
    public InscriptionResponse desinscrire(@PathVariable Long id) {
        return service.desinscrire(id);
    }
}
