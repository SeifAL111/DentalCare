package tn.esprit.dentalcare.dossiermedical.service;

/** Traduite en 403. Avec Keycloak (S8), l'identité viendra du token au lieu d'un paramètre. */
public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException(String message) {
        super(message);
    }
}
