package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.Allergie;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieRequest;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieResponse;
import tn.esprit.dentalcare.dossiermedical.repository.AllergieRepository;

import java.util.List;

@Service
@Transactional
public class AllergieService {

    private final DossierService dossierService;
    private final AllergieRepository allergieRepository;

    public AllergieService(DossierService dossierService, AllergieRepository allergieRepository) {
        this.dossierService = dossierService;
        this.allergieRepository = allergieRepository;
    }

    public AllergieResponse ajouter(Long dossierId, AllergieRequest requete) {
        DossierMedical dossier = dossierService.chargerActif(dossierId);
        Allergie allergie = new Allergie(dossier);
        appliquer(allergie, requete);
        allergieRepository.save(allergie);
        dossier.setStatutAllergies(StatutAllergies.RENSEIGNEES);
        return AllergieResponse.from(allergie);
    }

    @Transactional(readOnly = true)
    public List<AllergieResponse> lister(Long dossierId) {
        dossierService.chargerActif(dossierId);
        return allergieRepository.findByDossierIdOrderByCreeLeDesc(dossierId).stream()
                .map(AllergieResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AllergieResponse consulter(Long dossierId, Long allergieId) {
        return AllergieResponse.from(charger(dossierId, allergieId));
    }

    public AllergieResponse modifier(Long dossierId, Long allergieId, AllergieRequest requete) {
        Allergie allergie = charger(dossierId, allergieId);
        appliquer(allergie, requete);
        return AllergieResponse.from(allergie);
    }

    public void supprimer(Long dossierId, Long allergieId) {
        Allergie allergie = charger(dossierId, allergieId);
        DossierMedical dossier = allergie.getDossier();
        allergieRepository.delete(allergie);
        if (allergieRepository.countByDossierId(dossierId) == 0) {
            // Plus aucune allergie : on ne sait pas si c'était une erreur de saisie,
            // le statut redevient « non renseigné » jusqu'à une nouvelle vérification.
            dossier.setStatutAllergies(StatutAllergies.NON_RENSEIGNE);
        }
    }

    /** Vérifie que l'allergie appartient bien au dossier de l'URL (sinon 404). */
    private Allergie charger(Long dossierId, Long allergieId) {
        dossierService.chargerActif(dossierId);
        return allergieRepository.findByIdAndDossierId(allergieId, dossierId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Allergie " + allergieId + " introuvable dans le dossier " + dossierId + "."));
    }

    private static void appliquer(Allergie allergie, AllergieRequest requete) {
        allergie.setSubstance(requete.substance().trim());
        allergie.setCategorie(requete.categorie());
        allergie.setSeverite(requete.severite());
        allergie.setReaction(DossierService.nettoyer(requete.reaction()));
        allergie.setDeclareeLe(requete.declareeLe());
    }
}
