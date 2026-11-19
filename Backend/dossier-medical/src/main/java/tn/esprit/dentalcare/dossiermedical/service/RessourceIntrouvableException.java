package tn.esprit.dentalcare.dossiermedical.service;

/** Traduite en 404 par le gestionnaire d'erreurs. */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
