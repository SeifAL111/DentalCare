package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.dossiermedical.domain.Allergie;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.dto.FacteursRisqueResponse;
import tn.esprit.dentalcare.dossiermedical.dto.VerificationResponse;
import tn.esprit.dentalcare.dossiermedical.repository.AllergieRepository;
import tn.esprit.dentalcare.dossiermedical.repository.AntecedentRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Les deux questions que les autres microservices posent au Dossier Médical (OpenFeign en S6) :
 * « cette ordonnance est-elle contre-indiquée ? » (Odontogramme)
 * et « ce patient a-t-il un facteur de risque ? » (Suivi Post-opératoire).
 */
@Service
public class RisqueMedicalService {

    private final DossierService dossierService;
    private final AllergieRepository allergieRepository;
    private final AntecedentRepository antecedentRepository;
    private final MoteurContreIndications moteur;
    private final EvaluateurRisque evaluateur;

    public RisqueMedicalService(DossierService dossierService,
                                AllergieRepository allergieRepository,
                                AntecedentRepository antecedentRepository,
                                MoteurContreIndications moteur,
                                EvaluateurRisque evaluateur) {
        this.dossierService = dossierService;
        this.allergieRepository = allergieRepository;
        this.antecedentRepository = antecedentRepository;
        this.moteur = moteur;
        this.evaluateur = evaluateur;
    }

    @Transactional(readOnly = true)
    public VerificationResponse verifierPrescription(Long dossierId, String medicament) {
        return verifierPrescription(dossierService.chargerActif(dossierId), medicament);
    }

    @Transactional(readOnly = true)
    public VerificationResponse verifierPrescriptionParPatient(String patientId, String medicament) {
        return verifierPrescription(dossierService.chargerParPatient(patientId), medicament);
    }

    @Transactional(readOnly = true)
    public FacteursRisqueResponse facteursRisque(Long dossierId, TypeActe acte) {
        return evaluer(dossierService.chargerActif(dossierId), acte);
    }

    @Transactional(readOnly = true)
    public FacteursRisqueResponse facteursRisqueParPatient(String patientId, TypeActe acte) {
        return evaluer(dossierService.chargerParPatient(patientId), acte);
    }

    /** Sans @Transactional : s'exécute dans la transaction de l'appelant (ex. consultation d'un partage). */
    public FacteursRisqueResponse evaluer(DossierMedical dossier, TypeActe acte) {
        ResultatRisque resultat = evaluateur.evaluer(
                antecedentRepository.findByDossierIdAndActifTrue(dossier.getId()), acte);
        List<String> allergiesGraves = allergieRepository.findByDossierIdOrderByCreeLeDesc(dossier.getId()).stream()
                .filter(a -> a.getSeverite().estGrave())
                .map(a -> a.getSubstance() + " (" + a.getSeverite() + ")")
                .toList();
        return new FacteursRisqueResponse(
                dossier.getId(),
                dossier.getPatientId(),
                acte,
                resultat.niveauGlobal(),
                resultat.protocoleRenforce(),
                resultat.facteurs(),
                allergiesGraves,
                dossier.getStatutAllergies(),
                LocalDateTime.now());
    }

    private VerificationResponse verifierPrescription(DossierMedical dossier, String medicament) {
        List<Allergie> allergies = allergieRepository.findByDossierIdOrderByCreeLeDesc(dossier.getId());
        ResultatVerification resultat = moteur.evaluer(
                medicament.trim(),
                dossier.getStatutAllergies(),
                allergies,
                antecedentRepository.findByDossierIdAndActifTrue(dossier.getId()));
        return new VerificationResponse(
                dossier.getId(),
                dossier.getPatientId(),
                medicament.trim(),
                resultat.niveau(),
                resultat.famillesDetectees(),
                resultat.raisons(),
                resultat.niveau() != NiveauContreIndication.OK,
                LocalDateTime.now());
    }
}
