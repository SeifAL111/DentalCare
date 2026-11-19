package tn.esprit.dentalcare.agendaclinique.catalogue;

import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import org.springframework.stereotype.Component;

/**
 * Copie locale des durées du catalogue (mêmes codes et durées que les données de plan-traitement).
 * Sert de cache : l'agenda reste utilisable même si le Plan de Traitement ne répond pas.
 */
@Component
public class CatalogueActesLocal implements CatalogueActes {

    private static final Map<String, Integer> DUREES_MINUTES = Map.of(
            "DEVIT", 60,
            "EMPREINTE", 45,
            "POSE_COURONNE", 50,
            "DETARTRAGE", 30,
            "EXTRACTION", 40,
            "CONSULTATION", 20,
            "URGENCE", 30);

    @Override
    public OptionalInt dureeMinutes(String acteCode) {
        if (acteCode == null || acteCode.isBlank()) {
            return OptionalInt.empty();
        }
        Integer duree = DUREES_MINUTES.get(acteCode.trim().toUpperCase(Locale.ROOT));
        return duree == null ? OptionalInt.empty() : OptionalInt.of(duree);
    }
}
