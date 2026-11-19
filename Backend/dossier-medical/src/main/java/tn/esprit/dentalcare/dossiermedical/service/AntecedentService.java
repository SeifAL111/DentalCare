package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentRequest;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentResponse;
import tn.esprit.dentalcare.dossiermedical.repository.AntecedentRepository;

import java.util.List;

@Service
@Transactional
public class AntecedentService {

    private final DossierService dossierService;
    private final AntecedentRepository antecedentRepository;

    public AntecedentService(DossierService dossierService, AntecedentRepository antecedentRepository) {
        this.dossierService = dossierService;
        this.antecedentRepository = antecedentRepository;
    }

    public AntecedentResponse ajouter(Long dossierId, AntecedentRequest requete) {
        DossierMedical dossier = dossierService.chargerActif(dossierId);
        Antecedent antecedent = new Antecedent(dossier);
        appliquer(antecedent, requete);
        return AntecedentResponse.from(antecedentRepository.save(antecedent));
    }

    @Transactional(readOnly = true)
    public List<AntecedentResponse> lister(Long dossierId) {
        dossierService.chargerActif(dossierId);
        return antecedentRepository.findByDossierIdOrderByCreeLeDesc(dossierId).stream()
                .map(AntecedentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AntecedentResponse consulter(Long dossierId, Long antecedentId) {
        return AntecedentResponse.from(charger(dossierId, antecedentId));
    }

    public AntecedentResponse modifier(Long dossierId, Long antecedentId, AntecedentRequest requete) {
        Antecedent antecedent = charger(dossierId, antecedentId);
        appliquer(antecedent, requete);
        return AntecedentResponse.from(antecedent);
    }

    public void supprimer(Long dossierId, Long antecedentId) {
        antecedentRepository.delete(charger(dossierId, antecedentId));
    }

    private Antecedent charger(Long dossierId, Long antecedentId) {
        dossierService.chargerActif(dossierId);
        return antecedentRepository.findByIdAndDossierId(antecedentId, dossierId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Antécédent " + antecedentId + " introuvable dans le dossier " + dossierId + "."));
    }

    private static void appliquer(Antecedent antecedent, AntecedentRequest requete) {
        antecedent.setType(requete.type());
        antecedent.setDescription(requete.description().trim());
        antecedent.setFacteurRisque(requete.facteurRisque());
        antecedent.setDateDebut(requete.dateDebut());
        antecedent.setActif(requete.actif() == null || requete.actif());
    }
}
