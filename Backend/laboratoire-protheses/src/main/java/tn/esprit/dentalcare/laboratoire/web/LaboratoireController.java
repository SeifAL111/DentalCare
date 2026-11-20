package tn.esprit.dentalcare.laboratoire.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.dentalcare.laboratoire.domain.CommandeProthese;
import tn.esprit.dentalcare.laboratoire.domain.Laboratoire;
import tn.esprit.dentalcare.laboratoire.domain.Retouche;
import tn.esprit.dentalcare.laboratoire.domain.StatutProthese;
import tn.esprit.dentalcare.laboratoire.service.CommandeProtheseService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LaboratoireController {

    private final CommandeProtheseService service;

    // --- Endpoints Laboratoires ---

    @PostMapping("/laboratoires")
    public ResponseEntity<Laboratoire> creerLaboratoire(@RequestBody Laboratoire laboratoire) {
        return new ResponseEntity<>(service.creerLaboratoire(laboratoire), HttpStatus.CREATED);
    }

    @GetMapping("/laboratoires/{id}/commandes")
    public ResponseEntity<List<CommandeProthese>> listerCommandesParLaboratoire(@PathVariable Long id) {
        return ResponseEntity.ok(service.listerParLaboratoire(id));
    }

    // --- Endpoints Prothèses ---

    @PostMapping("/protheses")
    public ResponseEntity<CommandeProthese> creerCommande(
            @RequestBody CommandeProthese commande,
            @RequestParam Long laboratoireId) {
        return new ResponseEntity<>(service.creerCommande(commande, laboratoireId), HttpStatus.CREATED);
    }

    @GetMapping("/protheses/{id}")
    public ResponseEntity<CommandeProthese> getCommande(@PathVariable Long id) {
        return ResponseEntity.ok(service.trouverParId(id));
    }

    @PatchMapping("/protheses/{id}/statut")
    public ResponseEntity<CommandeProthese> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutProthese statut) {
        return ResponseEntity.ok(service.changerStatut(id, statut));
    }

    @PostMapping("/protheses/{id}/retouches")
    public ResponseEntity<Retouche> ajouterRetouche(
            @PathVariable Long id,
            @RequestParam String motif) {
        return new ResponseEntity<>(service.ajouterRetouche(id, motif), HttpStatus.CREATED);
    }

    @GetMapping("/protheses/en-retard")
    public ResponseEntity<List<CommandeProthese>> getCommandesEnRetard() {
        return ResponseEntity.ok(service.listerCommandesEnRetard());
    }
}