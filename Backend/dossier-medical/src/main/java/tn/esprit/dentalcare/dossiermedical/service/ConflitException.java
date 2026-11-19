package tn.esprit.dentalcare.dossiermedical.service;

/** Traduite en 409 : la demande contredit l'état actuel des données. */
public class ConflitException extends RuntimeException {

    public ConflitException(String message) {
        super(message);
    }
}
