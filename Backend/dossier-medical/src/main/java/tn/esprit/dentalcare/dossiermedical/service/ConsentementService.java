package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.Consentement;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.StatutConsentement;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementRequest;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.IntegriteConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationConsentementResponse;
import tn.esprit.dentalcare.dossiermedical.repository.ConsentementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ConsentementService {

    private final DossierService dossierService;
    private final ConsentementRepository consentementRepository;
    private final int validiteMois;

    public ConsentementService(DossierService dossierService,
                               ConsentementRepository consentementRepository,
                               @Value("${dossier.consentement.validite-mois:12}") int validiteMois) {
        this.dossierService = dossierService;
        this.consentementRepository = consentementRepository;
        this.validiteMois = validiteMois;
    }

    public ConsentementResponse enregistrer(Long dossierId, ConsentementRequest requete) {
        DossierMedical dossier = dossierService.chargerActif(dossierId);
        if (consentementValide(dossierId, requete.typeActe()).isPresent()) {
            throw new ConflitException("Un consentement valide existe déjà pour l'acte « "
                    + requete.typeActe().getLibelle() + " ». Révoquez-le avant d'en enregistrer un nouveau.");
        }
        // Tronqué à la seconde : la date relue en base doit redonner exactement la même empreinte.
        LocalDateTime signeLe = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String signataire = requete.signataire().trim();
        String versionDocument = DossierService.nettoyer(requete.versionDocument());
        String empreinte = empreinte(dossier.getPatientId(), requete.typeActe(), signataire,
                versionDocument, requete.texteInforme(), signeLe);

        Consentement consentement = new Consentement(dossier, requete.typeActe(), signataire,
                requete.praticienId().trim(), versionDocument, requete.texteInforme(), signeLe,
                signeLe.toLocalDate().plusMonths(validiteMois), empreinte);
        return ConsentementResponse.from(consentementRepository.save(consentement));
    }

    @Transactional(readOnly = true)
    public List<ConsentementResponse> lister(Long dossierId, TypeActe filtre) {
        dossierService.chargerActif(dossierId);
        List<Consentement> consentements = filtre == null
                ? consentementRepository.findByDossierIdOrderBySigneLeDesc(dossierId)
                : consentementRepository.findByDossierIdAndTypeActeOrderBySigneLeDesc(dossierId, filtre);
        return consentements.stream().map(ConsentementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ConsentementResponse consulter(Long dossierId, Long consentementId) {
        return ConsentementResponse.from(charger(dossierId, consentementId));
    }

    /** Un consentement signé ne se modifie pas et ne se supprime pas : il se révoque. */
    public ConsentementResponse revoquer(Long dossierId, Long consentementId, String motif) {
        Consentement consentement = charger(dossierId, consentementId);
        if (consentement.getStatut() == StatutConsentement.REVOQUE) {
            throw new ConflitException("Le consentement " + consentementId + " est déjà révoqué.");
        }
        consentement.revoquer(DossierService.nettoyer(motif));
        return ConsentementResponse.from(consentement);
    }

    /** Recalcule l'empreinte pour prouver que le document n'a pas été modifié après la signature. */
    @Transactional(readOnly = true)
    public IntegriteConsentementResponse verifierIntegrite(Long dossierId, Long consentementId) {
        Consentement c = charger(dossierId, consentementId);
        String recalculee = empreinte(c.getDossier().getPatientId(), c.getTypeActe(), c.getSignataire(),
                c.getVersionDocument(), c.getTexteInforme(), c.getSigneLe());
        return new IntegriteConsentementResponse(c.getId(), c.getEmpreinte(), recalculee,
                recalculee.equals(c.getEmpreinte()));
    }

    @Transactional(readOnly = true)
    public VerificationConsentementResponse verifier(Long dossierId, TypeActe acte) {
        return verifier(dossierService.chargerActif(dossierId), acte);
    }

    @Transactional(readOnly = true)
    public VerificationConsentementResponse verifierParPatient(String patientId, TypeActe acte) {
        return verifier(dossierService.chargerParPatient(patientId), acte);
    }

    /** Règle métier : consentement signé obligatoire avant tout acte invasif. */
    private VerificationConsentementResponse verifier(DossierMedical dossier, TypeActe acte) {
        Optional<Consentement> valide = consentementValide(dossier.getId(), acte);
        boolean autorise = !acte.isInvasif() || valide.isPresent();

        String message;
        if (valide.isPresent()) {
            message = "Consentement valide signé le " + valide.get().getSigneLe().toLocalDate()
                    + ", jusqu'au " + valide.get().getExpireLe() + ".";
        } else if (acte.isInvasif()) {
            message = "Consentement signé obligatoire avant l'acte invasif « " + acte.getLibelle() + " ».";
        } else {
            message = "Acte non invasif : consentement écrit non requis.";
        }

        return new VerificationConsentementResponse(
                dossier.getId(),
                dossier.getPatientId(),
                acte,
                acte.isInvasif(),
                valide.isPresent(),
                autorise,
                valide.map(Consentement::getId).orElse(null),
                valide.map(Consentement::getExpireLe).orElse(null),
                message);
    }

    private Optional<Consentement> consentementValide(Long dossierId, TypeActe acte) {
        LocalDate aujourdhui = LocalDate.now();
        return consentementRepository.findByDossierIdAndTypeActeOrderBySigneLeDesc(dossierId, acte).stream()
                .filter(c -> c.estValideAu(aujourdhui))
                .findFirst();
    }

    private Consentement charger(Long dossierId, Long consentementId) {
        dossierService.chargerActif(dossierId);
        return consentementRepository.findByIdAndDossierId(consentementId, dossierId)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Consentement " + consentementId + " introuvable dans le dossier " + dossierId + "."));
    }

    private static String empreinte(String patientId, TypeActe acte, String signataire, String versionDocument,
                                    String texte, LocalDateTime signeLe) {
        return Empreintes.sha256(patientId, acte.name(), signataire, String.valueOf(versionDocument), texte,
                signeLe.toString());
    }
}
