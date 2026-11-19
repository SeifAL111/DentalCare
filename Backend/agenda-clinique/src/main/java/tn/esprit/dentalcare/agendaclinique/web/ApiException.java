package tn.esprit.dentalcare.agendaclinique.web;

import org.springframework.http.HttpStatus;

/** Erreur métier traduite en code HTTP par ApiExceptionHandler (400, 404, 409, 410...). */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
