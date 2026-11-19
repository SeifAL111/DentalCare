package tn.esprit.dentalcare.dossiermedical.service;

import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;

/**
 * Une raison expliquée en clair, lisible par le dentiste comme par l'agent Clinique IA.
 */
public record RaisonContreIndication(
        Type type,
        NiveauContreIndication niveau,
        String detail) {

    public enum Type {
        /** Allergie à la même famille ou à la même substance. */
        ALLERGIE,
        /** Allergie à une famille proche (ex. pénicillines → céphalosporines). */
        REACTION_CROISEE,
        /** Interaction avec un traitement en cours ou une situation (grossesse…). */
        INTERACTION,
        /** Les allergies n'ont jamais été vérifiées. */
        ALLERGIES_NON_RENSEIGNEES,
        /** Information : le médicament n'est pas dans le référentiel. */
        MEDICAMENT_NON_REFERENCE
    }
}
