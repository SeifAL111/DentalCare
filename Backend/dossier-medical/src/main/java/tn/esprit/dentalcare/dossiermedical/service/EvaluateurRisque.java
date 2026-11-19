package tn.esprit.dentalcare.dossiermedical.service;

import org.springframework.stereotype.Component;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauGlobal;
import tn.esprit.dentalcare.dossiermedical.domain.NiveauRisque;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;

import java.util.List;

/**
 * Calcule les facteurs de risque d'un patient pour un acte donné.
 * Une extraction et un détartrage ne présentent pas les mêmes risques :
 * seuls les facteurs qui concernent l'acte sont retenus.
 */
@Component
public class EvaluateurRisque {

    public ResultatRisque evaluer(List<Antecedent> antecedentsActifs, TypeActe acte) {
        List<FacteurIdentifie> facteurs = antecedentsActifs.stream()
                .filter(a -> a.getFacteurRisque() != null)
                .filter(a -> a.getFacteurRisque().concerne(acte))
                .map(a -> new FacteurIdentifie(
                        a.getFacteurRisque(),
                        a.getFacteurRisque().getLibelle(),
                        a.getFacteurRisque().getNiveau(),
                        a.getFacteurRisque().getImpact(),
                        a.getId(),
                        a.getDescription()))
                .toList();

        NiveauGlobal niveau = NiveauGlobal.VERT;
        for (FacteurIdentifie facteur : facteurs) {
            if (facteur.niveau() == NiveauRisque.ELEVE) {
                niveau = NiveauGlobal.ROUGE;
                break;
            }
            niveau = NiveauGlobal.ORANGE;
        }

        return new ResultatRisque(niveau, niveau != NiveauGlobal.VERT, facteurs);
    }
}
