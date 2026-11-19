package tn.esprit.dentalcare.dossiermedical.domain;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Normalisation de texte pour comparer des noms de substances saisis librement :
 * « Pénicilline », « PENICILLINE » et « penicilline » deviennent identiques.
 */
public final class Textes {

    private Textes() {
    }

    /** Minuscules, sans accents, ponctuation remplacée par des espaces. */
    public static String normaliser(String texte) {
        if (texte == null) {
            return "";
        }
        String sansAccents = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();
    }

    /** Découpe un texte normalisé en mots. */
    public static List<String> mots(String texte) {
        String normalise = normaliser(texte);
        if (normalise.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(normalise.split(" "));
    }

    /**
     * Vrai si le texte contient le mot-clé : un mot-clé d'un seul mot doit commencer un mot du texte
     * (« sulfite » trouve « sulfites », mais « ains » ne trouve pas « bains ») ;
     * un mot-clé de plusieurs mots doit apparaître tel quel.
     */
    public static boolean contientMotCle(String texte, String motCle) {
        String cle = normaliser(motCle);
        if (cle.isEmpty()) {
            return false;
        }
        if (cle.contains(" ")) {
            return (" " + normaliser(texte) + " ").contains(" " + cle + " ");
        }
        return mots(texte).stream().anyMatch(mot -> mot.startsWith(cle));
    }
}
