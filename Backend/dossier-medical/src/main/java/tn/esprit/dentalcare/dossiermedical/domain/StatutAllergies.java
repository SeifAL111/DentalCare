package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Distingue « le patient n'a pas d'allergie » de « personne ne lui a demandé ».
 * Une liste vide ne suffit pas à conclure qu'un médicament est sans danger.
 */
public enum StatutAllergies {
    /** Les allergies n'ont jamais été vérifiées : toute prescription donne un avertissement. */
    NON_RENSEIGNE,
    /** Le praticien a vérifié : aucune allergie connue. */
    AUCUNE_CONNUE,
    /** Au moins une allergie est enregistrée. */
    RENSEIGNEES
}
