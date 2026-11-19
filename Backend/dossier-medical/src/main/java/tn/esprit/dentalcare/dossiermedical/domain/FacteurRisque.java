package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Facteurs de risque médicaux pertinents en dentaire, et les actes qu'ils concernent.
 * <p>
 * Données de démonstration : les seuils et impacts sont à faire valider par un praticien.
 */
public enum FacteurRisque {
    ANTICOAGULANT("Traitement anticoagulant", NiveauRisque.ELEVE,
            "Risque hémorragique", Cible.ACTES_SANGLANTS),
    BISPHOSPHONATES("Traitement par bisphosphonates", NiveauRisque.ELEVE,
            "Risque d'ostéonécrose des mâchoires", Cible.ACTES_CHIRURGICAUX),
    RADIOTHERAPIE_CERVICO_FACIALE("Radiothérapie cervico-faciale", NiveauRisque.ELEVE,
            "Risque d'ostéoradionécrose", Cible.ACTES_CHIRURGICAUX),
    CARDIOPATHIE_A_RISQUE("Cardiopathie à risque d'endocardite", NiveauRisque.ELEVE,
            "Antibioprophylaxie à discuter avant l'acte", Cible.ACTES_SANGLANTS),
    IMMUNODEPRESSION("Immunodépression", NiveauRisque.ELEVE,
            "Risque infectieux accru", Cible.ACTES_INVASIFS),
    DIABETE_DESEQUILIBRE("Diabète déséquilibré", NiveauRisque.MODERE,
            "Cicatrisation ralentie, risque infectieux", Cible.ACTES_INVASIFS),
    TABAC("Tabagisme", NiveauRisque.MODERE,
            "Cicatrisation retardée, risque d'échec implantaire", Cible.ACTES_CHIRURGICAUX),
    GROSSESSE("Grossesse", NiveauRisque.MODERE,
            "Radiographies et prescriptions à limiter", Cible.TOUS_LES_ACTES);

    /** Familles d'actes visées par un facteur de risque. */
    public enum Cible {
        TOUS_LES_ACTES,
        ACTES_INVASIFS,
        ACTES_SANGLANTS,
        ACTES_CHIRURGICAUX
    }

    private final String libelle;
    private final NiveauRisque niveau;
    private final String impact;
    private final Cible cible;

    FacteurRisque(String libelle, NiveauRisque niveau, String impact, Cible cible) {
        this.libelle = libelle;
        this.niveau = niveau;
        this.impact = impact;
        this.cible = cible;
    }

    /** Ce facteur compte-t-il pour cet acte ? Sans acte précisé, tous les facteurs comptent. */
    public boolean concerne(TypeActe acte) {
        if (acte == null) {
            return true;
        }
        return switch (cible) {
            case TOUS_LES_ACTES -> true;
            case ACTES_INVASIFS -> acte.isInvasif();
            case ACTES_SANGLANTS -> acte.isSanglant();
            case ACTES_CHIRURGICAUX -> acte.isChirurgical();
        };
    }

    public String getLibelle() {
        return libelle;
    }

    public NiveauRisque getNiveau() {
        return niveau;
    }

    public String getImpact() {
        return impact;
    }

    public Cible getCible() {
        return cible;
    }
}
