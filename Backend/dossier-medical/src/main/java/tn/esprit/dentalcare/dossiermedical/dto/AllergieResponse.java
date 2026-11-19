package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.Allergie;
import tn.esprit.dentalcare.dossiermedical.domain.CategorieAllergie;
import tn.esprit.dentalcare.dossiermedical.domain.FamilleMedicament;
import tn.esprit.dentalcare.dossiermedical.domain.Severite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * famillesReconnues montre au praticien comment le service a compris la substance saisie
 * (ex. « Pénicilline » → PENICILLINES). Vide si la substance est absente du référentiel.
 */
public record AllergieResponse(
        Long id,
        Long dossierId,
        String substance,
        CategorieAllergie categorie,
        Severite severite,
        String reaction,
        LocalDate declareeLe,
        LocalDateTime creeLe,
        Set<FamilleMedicament> famillesReconnues) {

    public static AllergieResponse from(Allergie a) {
        return new AllergieResponse(
                a.getId(),
                a.getDossier().getId(),
                a.getSubstance(),
                a.getCategorie(),
                a.getSeverite(),
                a.getReaction(),
                a.getDeclareeLe(),
                a.getCreeLe(),
                FamilleMedicament.detecter(a.getSubstance()));
    }
}
