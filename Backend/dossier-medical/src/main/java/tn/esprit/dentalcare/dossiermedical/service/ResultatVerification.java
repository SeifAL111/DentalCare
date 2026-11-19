package tn.esprit.dentalcare.dossiermedical.service;

import tn.esprit.dentalcare.dossiermedical.domain.FamilleMedicament;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauContreIndication;

import java.util.List;
import java.util.Set;

public record ResultatVerification(
        NiveauContreIndication niveau,
        Set<FamilleMedicament> famillesDetectees,
        List<RaisonContreIndication> raisons) {
}
