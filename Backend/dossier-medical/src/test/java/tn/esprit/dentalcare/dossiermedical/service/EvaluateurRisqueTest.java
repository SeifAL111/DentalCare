package tn.esprit.dentalcare.dossiermedical.service;

import org.junit.jupiter.api.Test;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauGlobal;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.domain.TypeAntecedent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluateurRisqueTest {

    private final EvaluateurRisque evaluateur = new EvaluateurRisque();
    private final DossierMedical dossier = new DossierMedical("patient-test");

    @Test
    void anticoagulant_estRougePourUneExtraction() {
        ResultatRisque r = evaluateur.evaluer(List.of(antecedent(FacteurRisque.ANTICOAGULANT)), TypeActe.EXTRACTION);

        assertThat(r.niveauGlobal()).isEqualTo(NiveauGlobal.ROUGE);
        assertThat(r.protocoleRenforce()).isTrue();
        assertThat(r.facteurs()).extracting(FacteurIdentifie::code).containsExactly(FacteurRisque.ANTICOAGULANT);
    }

    @Test
    void anticoagulant_neComptePasPourUnDetartrage() {
        ResultatRisque r = evaluateur.evaluer(List.of(antecedent(FacteurRisque.ANTICOAGULANT)), TypeActe.DETARTRAGE);

        assertThat(r.niveauGlobal()).isEqualTo(NiveauGlobal.VERT);
        assertThat(r.protocoleRenforce()).isFalse();
        assertThat(r.facteurs()).isEmpty();
    }

    @Test
    void tabac_estOrangePourUnImplant() {
        ResultatRisque r = evaluateur.evaluer(List.of(antecedent(FacteurRisque.TABAC)), TypeActe.IMPLANT);

        assertThat(r.niveauGlobal()).isEqualTo(NiveauGlobal.ORANGE);
        assertThat(r.protocoleRenforce()).isTrue();
    }

    @Test
    void sansActe_tousLesFacteursComptent() {
        ResultatRisque r = evaluateur.evaluer(
                List.of(antecedent(FacteurRisque.TABAC), antecedent(FacteurRisque.BISPHOSPHONATES)), null);

        assertThat(r.niveauGlobal()).isEqualTo(NiveauGlobal.ROUGE);
        assertThat(r.facteurs()).hasSize(2);
    }

    @Test
    void antecedentSansFacteur_estIgnore() {
        ResultatRisque r = evaluateur.evaluer(List.of(antecedent(null)), TypeActe.EXTRACTION);

        assertThat(r.niveauGlobal()).isEqualTo(NiveauGlobal.VERT);
    }

    private Antecedent antecedent(FacteurRisque facteur) {
        Antecedent antecedent = new Antecedent(dossier);
        antecedent.setType(TypeAntecedent.MEDICAL);
        antecedent.setDescription("Antécédent de test");
        antecedent.setFacteurRisque(facteur);
        return antecedent;
    }
}
