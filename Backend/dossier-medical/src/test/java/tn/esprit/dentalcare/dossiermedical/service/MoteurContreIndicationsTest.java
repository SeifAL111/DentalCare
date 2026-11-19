package tn.esprit.dentalcare.dossiermedical.service;

import org.junit.jupiter.api.Test;
import tn.esprit.dentalcare.dossiermedical.domain.Allergie;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.CategorieAllergie;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.FamilleMedicament;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;
import tn.esprit.dentalcare.dossiermedical.domain.Severite;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;
import tn.esprit.dentalcare.dossiermedical.domain.TypeAntecedent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires purs : aucune base, aucun contexte Spring.
 */
class MoteurContreIndicationsTest {

    private final MoteurContreIndications moteur = new MoteurContreIndications();
    private final DossierMedical dossier = new DossierMedical("patient-test");

    @Test
    void allergiePenicilline_bloqueAmoxicilline() {
        ResultatVerification r = moteur.evaluer("Amoxicilline 1 g", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Pénicilline", Severite.ANAPHYLAXIE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.BLOQUANT);
        assertThat(r.raisons()).extracting(RaisonContreIndication::type)
                .containsExactly(RaisonContreIndication.Type.ALLERGIE);
        assertThat(r.famillesDetectees()).containsExactly(FamilleMedicament.PENICILLINES);
    }

    @Test
    void allergiePenicilline_avertitPourCephalosporine() {
        ResultatVerification r = moteur.evaluer("Céfalexine", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Pénicilline", Severite.SEVERE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.AVERTISSEMENT);
        assertThat(r.raisons()).extracting(RaisonContreIndication::type)
                .containsExactly(RaisonContreIndication.Type.REACTION_CROISEE);
    }

    @Test
    void allergiePenicilline_laissePasserParacetamol() {
        ResultatVerification r = moteur.evaluer("Paracétamol 1 g", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Pénicilline", Severite.ANAPHYLAXIE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.OK);
        assertThat(r.raisons()).isEmpty();
    }

    @Test
    void allergieLegere_donneSeulementUnAvertissement() {
        ResultatVerification r = moteur.evaluer("Ibuprofène 400 mg", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Aspirine", Severite.LEGERE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.AVERTISSEMENT);
    }

    @Test
    void allergiesNonRenseignees_donnentUnAvertissement() {
        ResultatVerification r = moteur.evaluer("Paracétamol", StatutAllergies.NON_RENSEIGNE, List.of(), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.AVERTISSEMENT);
        assertThat(r.raisons()).extracting(RaisonContreIndication::type)
                .containsExactly(RaisonContreIndication.Type.ALLERGIES_NON_RENSEIGNEES);
    }

    @Test
    void aucuneAllergieConnue_laissePasser() {
        ResultatVerification r = moteur.evaluer("Paracétamol", StatutAllergies.AUCUNE_CONNUE, List.of(), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.OK);
    }

    @Test
    void allergieAuxSulfites_bloqueAnesthesiqueAdrenaline() {
        ResultatVerification r = moteur.evaluer("Articaïne adrénaline 1/100 000", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Sulfites", Severite.SEVERE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.BLOQUANT);
        assertThat(r.famillesDetectees())
                .contains(FamilleMedicament.ANESTHESIQUES_AMIDES, FamilleMedicament.SULFITES);
    }

    @Test
    void anticoagulant_etAins_donnentUneInteraction() {
        ResultatVerification r = moteur.evaluer("Ibuprofène 400 mg", StatutAllergies.AUCUNE_CONNUE, List.of(),
                List.of(antecedent(FacteurRisque.ANTICOAGULANT, "Apixaban 5 mg")));

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.AVERTISSEMENT);
        assertThat(r.raisons()).extracting(RaisonContreIndication::type)
                .containsExactly(RaisonContreIndication.Type.INTERACTION);
    }

    @Test
    void medicamentHorsReferentiel_estControleParLeNom() {
        ResultatVerification r = moteur.evaluer("Tramadol 50 mg", StatutAllergies.RENSEIGNEES,
                List.of(allergie("Tramadol", Severite.MODEREE)), List.of());

        assertThat(r.niveau()).isEqualTo(NiveauContreIndication.BLOQUANT);
        assertThat(r.raisons()).extracting(RaisonContreIndication::type)
                .contains(RaisonContreIndication.Type.ALLERGIE, RaisonContreIndication.Type.MEDICAMENT_NON_REFERENCE);
    }

    @Test
    void reconnaissanceDesFamilles_neConfondPasLesMots() {
        assertThat(FamilleMedicament.detecter("Bains de bouche")).doesNotContain(FamilleMedicament.AINS);
        assertThat(FamilleMedicament.detecter("AINS")).containsExactly(FamilleMedicament.AINS);
        assertThat(FamilleMedicament.detecter("PÉNICILLINES")).containsExactly(FamilleMedicament.PENICILLINES);
    }

    private Allergie allergie(String substance, Severite severite) {
        Allergie allergie = new Allergie(dossier);
        allergie.setSubstance(substance);
        allergie.setCategorie(CategorieAllergie.MEDICAMENT);
        allergie.setSeverite(severite);
        return allergie;
    }

    private Antecedent antecedent(FacteurRisque facteur, String description) {
        Antecedent antecedent = new Antecedent(dossier);
        antecedent.setType(TypeAntecedent.TRAITEMENT_EN_COURS);
        antecedent.setDescription(description);
        antecedent.setFacteurRisque(facteur);
        return antecedent;
    }
}
