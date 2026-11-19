package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Ce qu'un lien de partage laisse voir : on ne partage que le nécessaire (minimisation des données).
 */
public enum PorteePartage {
    /** Allergies uniquement : pour une prescription ou une urgence. */
    ALLERGIES,
    /** Allergies, antécédents actifs et facteurs de risque : pour un confrère. */
    RESUME_MEDICAL,
    /** Tout le dossier, consentements compris. */
    COMPLET
}
