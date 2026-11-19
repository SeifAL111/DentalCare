package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Component;
import tn.esprit.dentalcare.dossiermedical.domain.Allergie;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.FamilleMedicament;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;
import tn.esprit.dentalcare.dossiermedical.domain.Severite;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;
import tn.esprit.dentalcare.dossiermedical.domain.Textes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contrôle d'une prescription en trois niveaux : OK, AVERTISSEMENT, BLOQUANT.
 * <p>
 * Classe sans accès à la base : elle reçoit les données et rend un verdict,
 * ce qui la rend simple à tester unitairement.
 * Règles de démonstration, à faire valider par un praticien.
 */
@Component
public class MoteurContreIndications {

    /** Familles proches : une allergie à l'une rend l'autre risquée sans l'interdire. */
    private static final Map<FamilleMedicament, Set<FamilleMedicament>> REACTIONS_CROISEES = Map.of(
            FamilleMedicament.PENICILLINES, EnumSet.of(FamilleMedicament.CEPHALOSPORINES),
            FamilleMedicament.CEPHALOSPORINES, EnumSet.of(FamilleMedicament.PENICILLINES));

    private record Interaction(FacteurRisque facteur, FamilleMedicament famille, String message) {
    }

    private static final List<Interaction> INTERACTIONS = List.of(
            new Interaction(FacteurRisque.ANTICOAGULANT, FamilleMedicament.AINS,
                    "Patient sous anticoagulant : les AINS, aspirine comprise, majorent le risque hémorragique."),
            new Interaction(FacteurRisque.ANTICOAGULANT, FamilleMedicament.NITROIMIDAZOLES,
                    "Patient sous anticoagulant : le métronidazole peut potentialiser les AVK (surveiller l'INR)."),
            new Interaction(FacteurRisque.GROSSESSE, FamilleMedicament.AINS,
                    "Patiente enceinte : les AINS sont à éviter pendant la grossesse."));

    public ResultatVerification evaluer(String medicament,
                                        StatutAllergies statutAllergies,
                                        List<Allergie> allergies,
                                        List<Antecedent> antecedentsActifs) {
        Set<FamilleMedicament> famillesMedicament = FamilleMedicament.detecter(medicament);
        List<RaisonContreIndication> raisons = new ArrayList<>();

        if (statutAllergies == StatutAllergies.NON_RENSEIGNE) {
            raisons.add(new RaisonContreIndication(
                    RaisonContreIndication.Type.ALLERGIES_NON_RENSEIGNEES,
                    NiveauContreIndication.AVERTISSEMENT,
                    "Les allergies de ce patient n'ont jamais été vérifiées : interrogez-le avant de prescrire."));
        }

        for (Allergie allergie : allergies) {
            controlerAllergie(allergie, medicament, famillesMedicament, raisons);
        }

        for (Antecedent antecedent : antecedentsActifs) {
            for (Interaction interaction : INTERACTIONS) {
                if (interaction.facteur() == antecedent.getFacteurRisque()
                        && famillesMedicament.contains(interaction.famille())) {
                    raisons.add(new RaisonContreIndication(
                            RaisonContreIndication.Type.INTERACTION,
                            NiveauContreIndication.AVERTISSEMENT,
                            interaction.message() + " (antécédent : " + antecedent.getDescription() + ")"));
                }
            }
        }

        if (famillesMedicament.isEmpty()) {
            raisons.add(new RaisonContreIndication(
                    RaisonContreIndication.Type.MEDICAMENT_NON_REFERENCE,
                    NiveauContreIndication.OK,
                    "Médicament absent du référentiel : seule la correspondance de nom avec les allergies a été contrôlée."));
        }

        NiveauContreIndication niveau = raisons.stream()
                .map(RaisonContreIndication::niveau)
                .reduce(NiveauContreIndication.OK, NiveauContreIndication::lePlusGrave);

        return new ResultatVerification(niveau, famillesMedicament, List.copyOf(raisons));
    }

    private void controlerAllergie(Allergie allergie, String medicament,
                                   Set<FamilleMedicament> famillesMedicament,
                                   List<RaisonContreIndication> raisons) {
        Set<FamilleMedicament> famillesAllergie = FamilleMedicament.detecter(allergie.getSubstance());
        NiveauContreIndication niveauAllergie = allergie.getSeverite() == Severite.LEGERE
                ? NiveauContreIndication.AVERTISSEMENT
                : NiveauContreIndication.BLOQUANT;

        Set<FamilleMedicament> communes = EnumSet.noneOf(FamilleMedicament.class);
        communes.addAll(famillesAllergie);
        communes.retainAll(famillesMedicament);

        if (!communes.isEmpty()) {
            raisons.add(new RaisonContreIndication(
                    RaisonContreIndication.Type.ALLERGIE,
                    niveauAllergie,
                    "Allergie à « " + allergie.getSubstance() + " » (" + allergie.getSeverite() + ") : « "
                            + medicament + " » appartient à la même famille (" + libelles(communes) + ")."));
            return;
        }

        if (Textes.contientMotCle(medicament, allergie.getSubstance())) {
            raisons.add(new RaisonContreIndication(
                    RaisonContreIndication.Type.ALLERGIE,
                    niveauAllergie,
                    "Allergie à « " + allergie.getSubstance() + " » (" + allergie.getSeverite()
                            + ") : le nom correspond au médicament prescrit."));
            return;
        }

        Set<FamilleMedicament> croisees = EnumSet.noneOf(FamilleMedicament.class);
        for (FamilleMedicament famille : famillesAllergie) {
            croisees.addAll(REACTIONS_CROISEES.getOrDefault(famille, Set.of()));
        }
        croisees.retainAll(famillesMedicament);

        if (!croisees.isEmpty()) {
            raisons.add(new RaisonContreIndication(
                    RaisonContreIndication.Type.REACTION_CROISEE,
                    NiveauContreIndication.AVERTISSEMENT,
                    "Allergie à « " + allergie.getSubstance() + " » (" + allergie.getSeverite()
                            + ") : réaction croisée possible avec " + libelles(croisees) + "."));
        }
    }

    private static String libelles(Set<FamilleMedicament> familles) {
        return familles.stream().map(FamilleMedicament::getLibelle).collect(Collectors.joining(", "));
    }
}
