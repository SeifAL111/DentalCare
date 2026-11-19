package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Verdict d'une vérification de prescription, du moins grave au plus grave.
 * L'ordre des constantes sert à retenir le niveau le plus grave.
 */
public enum NiveauContreIndication {
    OK,
    AVERTISSEMENT,
    BLOQUANT;

    public NiveauContreIndication lePlusGrave(NiveauContreIndication autre) {
        return autre.ordinal() > this.ordinal() ? autre : this;
    }
}
