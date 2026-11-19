package tn.esprit.dentalcare.agendaclinique.catalogue;

import java.util.OptionalInt;

/**
 * « Quelle durée pour cet acte ? » — question posée au Plan de Traitement (propriétaire du catalogue).
 * S5 : implémentation locale (CatalogueActesLocal).
 * S6 : implémentation OpenFeign vers plan-traitement (GET /api/actes-catalogue/{code}),
 *      avec la version locale comme cache de repli si le Plan ne répond pas.
 */
public interface CatalogueActes {

    OptionalInt dureeMinutes(String acteCode);
}
