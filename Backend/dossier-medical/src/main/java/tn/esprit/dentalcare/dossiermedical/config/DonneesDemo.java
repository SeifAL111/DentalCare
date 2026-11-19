package tn.esprit.dentalcare.dossiermedical.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.dentalcare.dossiermedical.domain.CategorieAllergie;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.Severite;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.domain.TypeAntecedent;
import tn.esprit.dentalcare.dossiermedical.dto.AllergieRequest;
import tn.esprit.dentalcare.dossiermedical.dto.AntecedentRequest;
import tn.esprit.dentalcare.dossiermedical.dto.ConsentementRequest;
import tn.esprit.dentalcare.dossiermedical.dto.DossierCreateRequest;
import tn.esprit.dentalcare.dossiermedical.repository.DossierMedicalRepository;
import tn.esprit.dentalcare.dossiermedical.service.AllergieService;
import tn.esprit.dentalcare.dossiermedical.service.AntecedentService;
import tn.esprit.dentalcare.dossiermedical.service.ConsentementService;
import tn.esprit.dentalcare.dossiermedical.service.DossierService;

import java.time.LocalDate;

/**
 * Données fictives de démonstration, cohérentes avec le tableau de bord et le parcours d'Ahmed.
 * Passent par les services : les règles métier s'appliquent aussi aux données de démo.
 * Désactivable avec dossier.seed.enabled=false.
 */
@Component
public class DonneesDemo implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DonneesDemo.class);

    private final boolean actif;
    private final DossierMedicalRepository dossierRepository;
    private final DossierService dossierService;
    private final AllergieService allergieService;
    private final AntecedentService antecedentService;
    private final ConsentementService consentementService;

    public DonneesDemo(@Value("${dossier.seed.enabled:true}") boolean actif,
                       DossierMedicalRepository dossierRepository,
                       DossierService dossierService,
                       AllergieService allergieService,
                       AntecedentService antecedentService,
                       ConsentementService consentementService) {
        this.actif = actif;
        this.dossierRepository = dossierRepository;
        this.dossierService = dossierService;
        this.allergieService = allergieService;
        this.antecedentService = antecedentService;
        this.consentementService = consentementService;
    }

    @Override
    public void run(String... args) {
        if (!actif || dossierRepository.count() > 0) {
            return;
        }

        // Ahmed — parcours de démonstration : couronne sur la 36. Allergie grave à la pénicilline.
        Long ahmed = dossier("patient-ahmed", "O+", "Parcours de démonstration : couronne sur la dent 36.");
        allergieService.ajouter(ahmed, new AllergieRequest("Pénicilline", CategorieAllergie.MEDICAMENT,
                Severite.ANAPHYLAXIE, "Œdème de Quincke après une prise d'amoxicilline", LocalDate.of(2019, 6, 12)));
        consentement(ahmed, TypeActe.DEVITALISATION, "Ahmed", "praticien-moreau",
                "J'ai été informé du déroulement de la dévitalisation de la dent 36, de ses risques "
                        + "(douleurs post-opératoires, fracture de la dent) et des alternatives.");

        // Élodie Mercier — extraction de la 38 ; sous anticoagulant (explique l'alerte « saignement persistant »).
        Long elodie = dossier("patient-elodie-mercier", "A+", null);
        dossierService.declarerAucuneAllergie(elodie);
        antecedentService.ajouter(elodie, new AntecedentRequest(TypeAntecedent.TRAITEMENT_EN_COURS,
                "Apixaban 5 mg, deux fois par jour (fibrillation atriale)", FacteurRisque.ANTICOAGULANT,
                LocalDate.of(2024, 3, 1), true));
        consentement(elodie, TypeActe.EXTRACTION, "Élodie Mercier", "praticien-moreau",
                "J'ai été informée du déroulement de l'extraction de la dent 38 et du risque de saignement "
                        + "prolongé lié à mon traitement anticoagulant.");

        // Camille Dubois — pose d'implant (36) ; allergie au latex (matériau dentaire).
        Long camille = dossier("patient-camille-dubois", "B+", null);
        allergieService.ajouter(camille, new AllergieRequest("Latex", CategorieAllergie.MATERIAU,
                Severite.MODEREE, "Urticaire de contact avec des gants en latex", null));
        consentement(camille, TypeActe.IMPLANT, "Camille Dubois", "praticien-moreau",
                "J'ai été informée du déroulement de la pose d'implant, des risques (infection, échec "
                        + "d'ostéo-intégration, atteinte nerveuse) et des alternatives (bridge, prothèse amovible).");

        // Paul Henry — implant 46 ; fumeur et diabétique ; allergies jamais vérifiées.
        Long paul = dossier("patient-paul-henry", null, null);
        antecedentService.ajouter(paul, new AntecedentRequest(TypeAntecedent.HABITUDE_DE_VIE,
                "Tabac, environ 15 cigarettes par jour", FacteurRisque.TABAC, null, true));
        antecedentService.ajouter(paul, new AntecedentRequest(TypeAntecedent.MEDICAL,
                "Diabète de type 2, HbA1c à 8,5 %", FacteurRisque.DIABETE_DESEQUILIBRE,
                LocalDate.of(2018, 1, 15), true));
        consentement(paul, TypeActe.IMPLANT, "Paul Henry", "praticien-moreau",
                "J'ai été informé du déroulement de la pose d'implant et du risque d'échec accru "
                        + "lié au tabac et au diabète.");

        // Julien Roux — surfaçage ; allergie aux sulfites (bloque les anesthésiques adrénalinés).
        Long julien = dossier("patient-julien-roux", "AB-", null);
        allergieService.ajouter(julien, new AllergieRequest("Sulfites", CategorieAllergie.AUTRE,
                Severite.SEVERE, "Crise d'asthme sévère", null));
        consentement(julien, TypeActe.SURFACAGE, "Julien Roux", "praticien-laurent",
                "J'ai été informé du déroulement du surfaçage radiculaire des quadrants 1 à 4 "
                        + "et des suites possibles (sensibilité, saignement).");

        LOG.info("Données de démonstration du Dossier Médical chargées : 5 patients.");
    }

    private Long dossier(String patientId, String groupeSanguin, String observations) {
        return dossierService.creer(new DossierCreateRequest(patientId, groupeSanguin, null, null, null,
                observations)).id();
    }

    private void consentement(Long dossierId, TypeActe acte, String signataire, String praticienId, String texte) {
        consentementService.enregistrer(dossierId,
                new ConsentementRequest(acte, signataire, praticienId, "v1", texte));
    }
}
