package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.TypeAntecedent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AntecedentResponse(
        Long id,
        Long dossierId,
        TypeAntecedent type,
        String description,
        FacteurRisque facteurRisque,
        LocalDate dateDebut,
        boolean actif,
        LocalDateTime creeLe) {

    public static AntecedentResponse from(Antecedent a) {
        return new AntecedentResponse(
                a.getId(),
                a.getDossier().getId(),
                a.getType(),
                a.getDescription(),
                a.getFacteurRisque(),
                a.getDateDebut(),
                a.isActif(),
                a.getCreeLe());
    }
}
