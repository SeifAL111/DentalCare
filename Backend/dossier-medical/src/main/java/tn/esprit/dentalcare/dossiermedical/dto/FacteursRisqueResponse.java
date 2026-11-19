package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.NiveauGlobal;
import tn.esprit.dentalcare.dossiermedical.domain.StatutAllergies;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;
import tn.esprit.dentalcare.dossiermedical.service.FacteurIdentifie;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Réponse lue par le Suivi Post-opératoire : protocoleRenforce lui dit d'appliquer le protocole renforcé.
 * acte est null quand l'évaluation porte sur tous les actes.
 */
public record FacteursRisqueResponse(
        Long dossierId,
        String patientId,
        TypeActe acte,
        NiveauGlobal niveauGlobal,
        boolean protocoleRenforce,
        List<FacteurIdentifie> facteurs,
        List<String> allergiesGraves,
        StatutAllergies statutAllergies,
        LocalDateTime evalueLe) {
}
