package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * SIGNE et REVOQUE sont enregistrés en base ; EXPIRE est calculé à la lecture
 * à partir de la date d'expiration, sans tâche planifiée.
 */
public enum StatutConsentement {
    SIGNE,
    REVOQUE,
    EXPIRE
}
