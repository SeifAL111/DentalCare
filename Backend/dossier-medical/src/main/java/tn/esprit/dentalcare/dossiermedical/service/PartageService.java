package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.JournalAcces;
import tn.esprit.dentalcare.dossiermedical.domain.PartageTemporaire;
import tn.esprit.dentalcare.dossiermedical.domain.PorteePartage;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieResponse;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentResponse;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.DossierPartageResponse;
import tn.esprit.dentalcare.dossiermedical.dto.JournalAccesResponse;
import tn.esprit.dentalcare.dossiermedical.dto.PartageRequest;
import tn.esprit.dentalcare.dossiermedical.dto.PartageResponse;
import tn.esprit.dentalcare.dossiermedical.repository.AllergieRepository;
import tn.esprit.dentalcare.dossiermedical.repository.AntecedentRepository;
import tn.esprit.dentalcare.dossiermedical.repository.ConsentementRepository;
import tn.esprit.dentalcare.dossiermedical.repository.JournalAccesRepository;
import tn.esprit.dentalcare.dossiermedical.repository.PartageTemporaireRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class PartageService {

    private static final SecureRandom ALEATOIRE = new SecureRandom();

    private final DossierService dossierService;
    private final PartageTemporaireRepository partageRepository;
    private final JournalAccesRepository journalRepository;
    private final AllergieRepository allergieRepository;
    private final AntecedentRepository antecedentRepository;
    private final ConsentementRepository consentementRepository;
    private final RisqueMedicalService risqueMedicalService;

    public PartageService(DossierService dossierService,
                          PartageTemporaireRepository partageRepository,
                          JournalAccesRepository journalRepository,
                          AllergieRepository allergieRepository,
                          AntecedentRepository antecedentRepository,
                          ConsentementRepository consentementRepository,
                          RisqueMedicalService risqueMedicalService) {
        this.dossierService = dossierService;
        this.partageRepository = partageRepository;
        this.journalRepository = journalRepository;
        this.allergieRepository = allergieRepository;
        this.antecedentRepository = antecedentRepository;
        this.consentementRepository = consentementRepository;
        this.risqueMedicalService = risqueMedicalService;
    }

    public PartageResponse creer(Long dossierId, PartageRequest requete) {
        DossierMedical dossier = dossierService.chargerActif(dossierId);
        LocalDateTime maintenant = LocalDateTime.now();
        PartageTemporaire partage = new PartageTemporaire(
                dossier,
                genererJeton(),
                requete.praticienId().trim(),
                requete.portee(),
                maintenant,
                maintenant.plusHours(requete.dureeHeures()),
                requete.nbConsultationsMax(),
                DossierService.nettoyer(requete.motif()));
        return PartageResponse.from(partageRepository.save(partage));
    }

    @Transactional(readOnly = true)
    public List<PartageResponse> lister(Long dossierId) {
        dossierService.chargerActif(dossierId);
        return partageRepository.findByDossierIdOrderByCreeLeDesc(dossierId).stream()
                .map(PartageResponse::from)
                .toList();
    }

    /** Révocation : le lien cesse de fonctionner mais reste dans l'historique. */
    public void revoquer(Long dossierId, Long partageId) {
        dossierService.chargerActif(dossierId);
        partageRepository.findByIdAndDossierId(partageId, dossierId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Partage " + partageId + " introuvable dans le dossier " + dossierId + "."))
                .revoquer();
    }

    /**
     * Ouverture d'un lien de partage. Chaque consultation réussie est inscrite au journal d'accès.
     * Avant Keycloak (S8), le praticien se déclare par paramètre ; ensuite, il viendra du token.
     */
    public DossierPartageResponse consulter(String token, String praticienId, String adresse) {
        PartageTemporaire partage = partageRepository.findByToken(token)
                .orElseThrow(() -> new RessourceIntrouvableException("Lien de partage inconnu."));
        DossierMedical dossier = partage.getDossier();

        if (!partage.estUtilisable(LocalDateTime.now()) || dossier.isArchive()) {
            throw new RessourceExpireeException("Ce lien de partage a expiré, a été révoqué ou a atteint "
                    + "son nombre maximum de consultations.");
        }
        if (!partage.getPraticienId().equals(praticienId.trim())) {
            throw new AccesRefuseException("Ce lien de partage est destiné à un autre praticien.");
        }

        partage.enregistrerConsultation();
        journalRepository.save(new JournalAcces(dossier, partage.getId(), praticienId.trim(), adresse,
                partage.getPortee()));

        return construireVue(dossier, partage);
    }

    @Transactional(readOnly = true)
    public List<JournalAccesResponse> journal(Long dossierId) {
        dossierService.chargerActif(dossierId);
        return journalRepository.findByDossierIdOrderByAccedeLeDesc(dossierId).stream()
                .map(JournalAccesResponse::from)
                .toList();
    }

    /** Ne renvoie que ce que la portée autorise ; le reste reste à null et disparaît du JSON. */
    private DossierPartageResponse construireVue(DossierMedical dossier, PartageTemporaire partage) {
        PorteePartage portee = partage.getPortee();
        boolean resume = portee == PorteePartage.RESUME_MEDICAL || portee == PorteePartage.COMPLET;
        boolean complet = portee == PorteePartage.COMPLET;
        Long id = dossier.getId();

        List<AllergieResponse> allergies = allergieRepository.findByDossierIdOrderByCreeLeDesc(id).stream()
                .map(AllergieResponse::from).toList();
        List<AntecedentResponse> antecedents = !resume ? null
                : (complet ? antecedentRepository.findByDossierIdOrderByCreeLeDesc(id)
                           : antecedentRepository.findByDossierIdAndActifTrue(id))
                  .stream().map(AntecedentResponse::from).toList();
        List<ConsentementResponse> consentements = !complet ? null
                : consentementRepository.findByDossierIdOrderBySigneLeDesc(id).stream()
                  .map(ConsentementResponse::from).toList();
        Integer restantes = partage.getNbConsultationsMax() == null ? null
                : partage.getNbConsultationsMax() - partage.getNbConsultations();

        return new DossierPartageResponse(
                dossier.getPatientId(),
                portee,
                partage.getExpireLe(),
                restantes,
                dossier.getStatutAllergies(),
                allergies,
                antecedents,
                resume ? risqueMedicalService.evaluer(dossier, null) : null,
                complet ? dossier.getGroupeSanguin() : null,
                complet ? dossier.getContactUrgenceNom() : null,
                complet ? dossier.getContactUrgenceTelephone() : null,
                consentements);
    }

    /** 32 octets aléatoires encodés en Base64 URL : impossible à deviner. */
    private static String genererJeton() {
        byte[] octets = new byte[32];
        ALEATOIRE.nextBytes(octets);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
    }
}
