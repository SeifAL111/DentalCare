package tn.esprit.dentalcare.laboratoire.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.laboratoire.domain.CommandeProthese;
import tn.esprit.dentalcare.laboratoire.domain.Laboratoire;
import tn.esprit.dentalcare.laboratoire.domain.Retouche;
import tn.esprit.dentalcare.laboratoire.domain.StatutProthese;
import tn.esprit.dentalcare.laboratoire.repository.CommandeProtheseRepository;
import tn.esprit.dentalcare.laboratoire.repository.LaboratoireRepository;
import tn.esprit.dentalcare.laboratoire.repository.RetoucheRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommandeProtheseService {

    private final CommandeProtheseRepository commandeRepository;
    private final LaboratoireRepository laboratoireRepository;
    private final RetoucheRepository retoucheRepository;

    // Machine à états : définit les transitions autorisées (EN_ATTENTE -> ACCEPTEE -> EN_FABRICATION -> CONTROLE -> PRETE -> LIVREE -> POSEE + RETOUCHE)
    private static final Map<StatutProthese, Set<StatutProthese>> TRANSITIONS_AUTORISEES = Map.of(
            StatutProthese.EN_ATTENTE, Set.of(StatutProthese.ACCEPTEE),
            StatutProthese.ACCEPTEE, Set.of(StatutProthese.EN_FABRICATION),
            StatutProthese.EN_FABRICATION, Set.of(StatutProthese.CONTROLE),
            StatutProthese.CONTROLE, Set.of(StatutProthese.PRETE, StatutProthese.RETOUCHE),
            StatutProthese.PRETE, Set.of(StatutProthese.LIVREE),
            StatutProthese.LIVREE, Set.of(StatutProthese.POSEE, StatutProthese.RETOUCHE),
            StatutProthese.POSEE, Set.of(StatutProthese.RETOUCHE),
            StatutProthese.RETOUCHE, Set.of(StatutProthese.EN_FABRICATION)
    );

    // 1. Créer un laboratoire (utile pour les tests initiaux)
    public Laboratoire creerLaboratoire(Laboratoire laboratoire) {
        return laboratoireRepository.save(laboratoire);
    }

    // 2. Créer une nouvelle commande de prothèse
    @Transactional
    public CommandeProthese creerCommande(CommandeProthese commande, Long laboratoireId) {
        Laboratoire lab = laboratoireRepository.findById(laboratoireId)
                .orElseThrow(() -> new IllegalArgumentException("Laboratoire introuvable : " + laboratoireId));

        commande.setLaboratoire(lab);
        commande.setStatut(StatutProthese.EN_ATTENTE);
        commande.setDateDemande(LocalDate.now());

        return commandeRepository.save(commande);
    }

    // 3. Récupérer une commande par ID
    public CommandeProthese trouverParId(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable : " + id));
    }

    // 4. Changer le statut avec garde de transition
    @Transactional
    public CommandeProthese changerStatut(Long id, StatutProthese nouveauStatut) {
        CommandeProthese commande = trouverParId(id);
        StatutProthese statutActuel = commande.getStatut();

        Set<StatutProthese> autorises = TRANSITIONS_AUTORISEES.getOrDefault(statutActuel, Set.of());
        if (!autorises.contains(nouveauStatut)) {
            throw new IllegalStateException(
                    "Transition interdite de " + statutActuel + " vers " + nouveauStatut
            );
        }

        commande.setStatut(nouveauStatut);
        return commandeRepository.save(commande);
    }

    // 5. Demander une retouche sur une commande
    @Transactional
    public Retouche ajouterRetouche(Long commandeId, String motif) {
        CommandeProthese commande = trouverParId(commandeId);

        Retouche retouche = Retouche.builder()
                .motif(motif)
                .dateDemandeRetouche(LocalDate.now())
                .commandeProthese(commande)
                .build();

        commande.setStatut(StatutProthese.RETOUCHE);
        commandeRepository.save(commande);

        return retoucheRepository.save(retouche);
    }

    // 6. Lister les commandes d'un laboratoire spécifique
    public List<CommandeProthese> listerParLaboratoire(Long laboratoireId) {
        return commandeRepository.findByLaboratoireId(laboratoireId);
    }

    // 7. Détecter les commandes en retard (datePromesse dépassée et non livrée/posée)
    public List<CommandeProthese> listerCommandesEnRetard() {
        LocalDate aujourdhui = LocalDate.now();
        return commandeRepository.findAll().stream()
                .filter(c -> c.getDatePromesse() != null && c.getDatePromesse().isBefore(aujourdhui))
                .filter(c -> c.getStatut() != StatutProthese.LIVREE && c.getStatut() != StatutProthese.POSEE)
                .toList();
    }
}