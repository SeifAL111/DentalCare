package tn.esprit.dentalcare.agendaclinique.web;

import org.springframework.http.HttpStatus;

/**
 * 410 Gone : l'offre de créneau a dépassé ses 15 minutes.
 * Type dédié pour que la transaction soit quand même validée (noRollbackFor) :
 * l'offre passe à EXPIREE et le créneau est proposé au patient suivant.
 */
public class OffreExpireeException extends ApiException {

    public OffreExpireeException(Long offreId) {
        super(HttpStatus.GONE, "Offre " + offreId + " expirée : le créneau a été proposé au patient suivant");
    }
}
