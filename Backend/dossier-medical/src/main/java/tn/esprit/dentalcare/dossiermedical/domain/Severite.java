package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Sévérité d'une allergie.
 * LEGERE produit un avertissement ; à partir de MODEREE, le médicament est bloqué.
 */
public enum Severite {
    LEGERE,
    MODEREE,
    SEVERE,
    ANAPHYLAXIE;

    public boolean estGrave() {
        return this == SEVERE || this == ANAPHYLAXIE;
    }
}
