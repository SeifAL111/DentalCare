package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Indicateur global « feu tricolore » d'un patient pour un acte donné.
 * VERT : aucun facteur · ORANGE : facteurs modérés · ROUGE : au moins un facteur élevé.
 */
public enum NiveauGlobal {
    VERT,
    ORANGE,
    ROUGE
}
