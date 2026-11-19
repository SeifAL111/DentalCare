package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieResponse;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentResponse;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierCreateRequest;
import tn.esprit.dentalcare.dossiermedical.dto.DossierDetailResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierUpdateRequest;
import tn.esprit.dentalcare.dossiermedical.dto.PageResponse;
import tn.esprit.dentalcare.dossiermedical.repository.AllergieRepository;
import tn.esprit.dentalcare.dossiermedical.repository.AntecedentRepository;
import tn.esprit.dentalcare.dossiermedical.repository.ConsentementRepository;
import tn.esprit.dentalcare.dossiermedical.repository.DossierMedicalRepository;

@Service
@Transactional
public class DossierService {

    private static final int TAILLE_MAX_PAGE = 100;

    private final DossierMedicalRepository dossierRepository;
    private final AllergieRepository allergieRepository;
    private final AntecedentRepository antecedentRepository;
    private final ConsentementRepository consentementRepository;

    public DossierService(DossierMedicalRepository dossierRepository,
                          AllergieRepository allergieRepository,
                          AntecedentRepository antecedentRepository,
                          ConsentementRepository consentementRepository) {
        this.dossierRepository = dossierRepository;
        this.allergieRepository = allergieRepository;
        this.antecedentRepository = antecedentRepository;
        this.consentementRepository = consentementRepository;
    }

    public DossierResponse creer(DossierCreateRequest requete) {
        String patientId = requete.patientId().trim();
        if (dossierRepository.existsByPatientId(patientId)) {
            throw new ConflitException("Le patient « " + patientId + " » possède déjà un dossier médical.");
        }
        DossierMedical dossier = new DossierMedical(patientId);
        appliquer(dossier, requete.groupeSanguin(), requete.medecinTraitant(), requete.contactUrgenceNom(),
                requete.contactUrgenceTelephone(), requete.observations());
        return DossierResponse.from(dossierRepository.save(dossier));
    }

    @Transactional(readOnly = true)
    public PageResponse<DossierResponse> lister(String recherche, int page, int taille) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(1, Math.min(taille, TAILLE_MAX_PAGE)),
                Sort.by(Sort.Order.desc("creeLe"), Sort.Order.desc("id")));
        Page<DossierMedical> resultat = (recherche == null || recherche.isBlank())
                ? dossierRepository.findByArchiveFalse(pageable)
                : dossierRepository.findByArchiveFalseAndPatientIdContainingIgnoreCase(recherche.trim(), pageable);
        return PageResponse.of(resultat, DossierResponse::from);
    }

    @Transactional(readOnly = true)
    public DossierDetailResponse consulter(Long id) {
        return detail(chargerActif(id));
    }

    @Transactional(readOnly = true)
    public DossierDetailResponse consulterParPatient(String patientId) {
        return detail(chargerParPatient(patientId));
    }

    public DossierResponse modifier(Long id, DossierUpdateRequest requete) {
        DossierMedical dossier = chargerActif(id);
        if (!dossier.getVersion().equals(requete.version())) {
            throw new ConflitException("Le dossier a été modifié entre-temps (version actuelle : "
                    + dossier.getVersion() + "). Rechargez-le avant de le modifier.");
        }
        appliquer(dossier, requete.groupeSanguin(), requete.medecinTraitant(), requete.contactUrgenceNom(),
                requete.contactUrgenceTelephone(), requete.observations());
        // flush immédiat : la réponse contient la nouvelle version et la nouvelle date de mise à jour
        return DossierResponse.from(dossierRepository.saveAndFlush(dossier));
    }

    /** Archivage plutôt que suppression : un dossier médical doit être conservé. */
    public void archiver(Long id) {
        chargerActif(id).archiver();
    }

    /** Le praticien confirme avoir interrogé le patient : aucune allergie connue. */
    public DossierResponse declarerAucuneAllergie(Long id) {
        DossierMedical dossier = chargerActif(id);
        if (allergieRepository.countByDossierId(id) > 0) {
            throw new ConflitException("Ce dossier contient des allergies : supprimez-les d'abord si elles sont erronées.");
        }
        dossier.setStatutAllergies(StatutAllergies.AUCUNE_CONNUE);
        return DossierResponse.from(dossierRepository.saveAndFlush(dossier));
    }

    // --- Méthodes utilisées par les autres services du microservice ---

    public DossierMedical chargerActif(Long id) {
        return dossierRepository.findByIdAndArchiveFalse(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Dossier médical " + id + " introuvable."));
    }

    public DossierMedical chargerParPatient(String patientId) {
        return dossierRepository.findByPatientIdAndArchiveFalse(patientId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Aucun dossier médical pour le patient « " + patientId + " »."));
    }

    private DossierDetailResponse detail(DossierMedical dossier) {
        Long id = dossier.getId();
        return DossierDetailResponse.from(dossier,
                allergieRepository.findByDossierIdOrderByCreeLeDesc(id).stream().map(AllergieResponse::from).toList(),
                antecedentRepository.findByDossierIdOrderByCreeLeDesc(id).stream().map(AntecedentResponse::from).toList(),
                consentementRepository.findByDossierIdOrderBySigneLeDesc(id).stream().map(ConsentementResponse::from).toList());
    }

    private static void appliquer(DossierMedical dossier, String groupeSanguin, String medecinTraitant,
                                  String contactUrgenceNom, String contactUrgenceTelephone, String observations) {
        dossier.setGroupeSanguin(nettoyer(groupeSanguin));
        dossier.setMedecinTraitant(nettoyer(medecinTraitant));
        dossier.setContactUrgenceNom(nettoyer(contactUrgenceNom));
        dossier.setContactUrgenceTelephone(nettoyer(contactUrgenceTelephone));
        dossier.setObservations(nettoyer(observations));
    }

    /** Chaîne vide ou blanche → null, sinon texte sans espaces superflus. */
    static String nettoyer(String valeur) {
        return (valeur == null || valeur.isBlank()) ? null : valeur.trim();
    }
}
