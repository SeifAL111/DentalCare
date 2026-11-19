package tn.esprit.dentalcare.dossiermedical.domain;

/**
 * Types d'actes connus du Dossier Médical, avec leurs caractéristiques de risque.
 * <p>
 * Les codes doivent rester alignés avec l'ActeCatalogue du service Plan de Traitement :
 * le Dossier ne lit pas sa base, il partage seulement le même vocabulaire.
 */
public enum TypeActe {
    CONSULTATION("Consultation", false, false, false),
    BILAN_PARODONTAL("Bilan parodontal", false, false, false),
    DETARTRAGE("Détartrage", false, false, false),
    SOIN_CARIE("Soin de carie", false, false, false),
    POSE_COURONNE("Pose de couronne", false, false, false),
    DEVITALISATION("Dévitalisation", true, false, false),
    SURFACAGE("Surfaçage radiculaire", true, true, false),
    EXTRACTION("Extraction", true, true, true),
    IMPLANT("Pose d'implant", true, true, true),
    GREFFE_GINGIVALE("Greffe gingivale", true, true, true);

    private final String libelle;
    /** Un acte invasif exige un consentement signé. */
    private final boolean invasif;
    /** Un acte sanglant est concerné par le risque hémorragique. */
    private final boolean sanglant;
    /** Un acte chirurgical touche l'os ou la gencive en profondeur. */
    private final boolean chirurgical;

    TypeActe(String libelle, boolean invasif, boolean sanglant, boolean chirurgical) {
        this.libelle = libelle;
        this.invasif = invasif;
        this.sanglant = sanglant;
        this.chirurgical = chirurgical;
    }

    public String getLibelle() {
        return libelle;
    }

    public boolean isInvasif() {
        return invasif;
    }

    public boolean isSanglant() {
        return sanglant;
    }

    public boolean isChirurgical() {
        return chirurgical;
    }
}
