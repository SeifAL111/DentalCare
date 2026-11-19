package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;

import java.time.LocalDate;

/**
 * Réponse à « puis-je réaliser cet acte ? » du point de vue du consentement.
 * autorise est faux seulement si l'acte est invasif et qu'aucun consentement valide n'existe.
 */
public record VerificationConsentementResponse(
        Long dossierId,
        String patientId,
        TypeActe acte,
        boolean acteInvasif,
        boolean consentementValide,
        boolean autorise,
        Long consentementId,
        LocalDate expireLe,
        String message) {
}
