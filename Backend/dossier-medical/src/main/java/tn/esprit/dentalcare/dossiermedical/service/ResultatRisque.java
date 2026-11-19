package tn.esprit.dentalcare.dossiermedical.service;

import tn.esprit.dentalcare.dossiermedical.domain.NiveauGlobal;

import java.util.List;

public record ResultatRisque(
        NiveauGlobal niveauGlobal,
        boolean protocoleRenforce,
        List<FacteurIdentifie> facteurs) {
}
