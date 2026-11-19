package tn.esprit.dentalcare.dossiermedical.service;

/** Traduite en 410 Gone : le lien a existé mais n'est plus utilisable. */
public class RessourceExpireeException extends RuntimeException {

    public RessourceExpireeException(String message) {
        super(message);
    }
}
