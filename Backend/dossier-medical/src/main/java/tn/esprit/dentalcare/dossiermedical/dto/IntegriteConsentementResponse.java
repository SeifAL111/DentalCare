package tn.esprit.dentalcare.dossiermedical.dto;

/**
 * integre est vrai si l'empreinte recalculée à partir des données enregistrées
 * est identique à celle calculée au moment de la signature.
 */
public record IntegriteConsentementResponse(
        Long consentementId,
        String empreinteEnregistree,
        String empreinteRecalculee,
        boolean integre) {
}
