package tn.esprit.dentalcare.dossiermedical.domain;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Référentiel simplifié des familles de molécules utilisées en cabinet dentaire.
 * Il sert à reconnaître qu'une allergie à la « pénicilline » interdit l'« amoxicilline ».
 * <p>
 * Données de démonstration, à faire valider par un praticien avant tout usage réel.
 */
public enum FamilleMedicament {
    PENICILLINES("Pénicillines",
            "penicilline", "amoxicilline", "ampicilline", "oxacilline", "cloxacilline", "augmentin", "clamoxyl"),
    CEPHALOSPORINES("Céphalosporines",
            "cephalosporine", "cefalexine", "cefaclor", "cefadroxil", "cefuroxime", "cefpodoxime", "cefixime",
            "ceftriaxone"),
    MACROLIDES("Macrolides",
            "macrolide", "azithromycine", "clarithromycine", "erythromycine", "spiramycine"),
    LINCOSAMIDES("Lincosamides",
            "clindamycine", "lincomycine"),
    NITROIMIDAZOLES("Nitro-imidazolés",
            "metronidazole", "ornidazole"),
    AINS("Anti-inflammatoires non stéroïdiens (AINS)",
            "ains", "ibuprofene", "ketoprofene", "diclofenac", "naproxene", "celecoxib", "aspirine",
            "acide acetylsalicylique", "advil", "nurofen"),
    PARACETAMOL("Paracétamol",
            "paracetamol", "doliprane", "efferalgan", "dafalgan"),
    ANESTHESIQUES_AMIDES("Anesthésiques locaux de type amide",
            "articaine", "lidocaine", "mepivacaine", "prilocaine", "bupivacaine"),
    ANESTHESIQUES_ESTERS("Anesthésiques locaux de type ester",
            "procaine", "benzocaine", "tetracaine"),
    SULFITES("Sulfites (antioxydant présent dans les anesthésiques adrénalinés)",
            "sulfite", "metabisulfite", "adrenaline", "epinephrine"),
    CHLORHEXIDINE("Chlorhexidine",
            "chlorhexidine");

    private final String libelle;
    private final List<String> motsCles;

    FamilleMedicament(String libelle, String... motsCles) {
        this.libelle = libelle;
        this.motsCles = List.of(motsCles);
    }

    public String getLibelle() {
        return libelle;
    }

    public List<String> getMotsCles() {
        return motsCles;
    }

    /** Toutes les familles reconnues dans un nom de médicament ou de substance. */
    public static Set<FamilleMedicament> detecter(String texte) {
        Set<FamilleMedicament> familles = EnumSet.noneOf(FamilleMedicament.class);
        for (FamilleMedicament famille : values()) {
            for (String motCle : famille.motsCles) {
                if (Textes.contientMotCle(texte, motCle)) {
                    familles.add(famille);
                    break;
                }
            }
        }
        return familles;
    }
}
