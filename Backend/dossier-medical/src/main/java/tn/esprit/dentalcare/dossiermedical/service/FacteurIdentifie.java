package tn.esprit.dentalcare.dossiermedical.service;

import tn.esprit.dentalcare.dossiermedical.domain.FacteurRisque;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauRisque;

/**
 * Un facteur de risque trouvé dans le dossier, relié à l'antécédent qui l'a déclenché.
 */
public record FacteurIdentifie(
        FacteurRisque code,
        String libelle,
        NiveauRisque niveau,
        String impact,
        Long antecedentId,
        String description) {
}
