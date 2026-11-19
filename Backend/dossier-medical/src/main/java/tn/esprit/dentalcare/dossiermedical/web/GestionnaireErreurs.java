package tn.esprit.dentalcare.dossiermedical.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tn.esprit.dentalcare.dossiermedical.dto.ProblemeApi;
import tn.esprit.dentalcare.dossiermedical.service.AccesRefuseException;
import tn.esprit.dentalcare.dossiermedical.service.ConflitException;
import tn.esprit.dentalcare.dossiermedical.service.RessourceExpireeException;
import tn.esprit.dentalcare.dossiermedical.service.RessourceIntrouvableException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Toutes les erreurs suivent le format RFC 9457 (application/problem+json) :
 * { "type", "title", "status", "detail", "instance" } + "erreurs" pour la validation.
 * Hériter de ResponseEntityExceptionHandler met aussi au format ProblemDetail les erreurs de Spring MVC
 * (JSON mal formé, valeur d'énumération inconnue, méthode non autorisée…).
 */
@RestControllerAdvice
public class GestionnaireErreurs extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ProblemeApi> introuvable(RessourceIntrouvableException ex, HttpServletRequest requete) {
        return probleme(HttpStatus.NOT_FOUND, "Ressource introuvable", ex.getMessage(), requete.getRequestURI(), null);
    }

    @ExceptionHandler(ConflitException.class)
    public ResponseEntity<ProblemeApi> conflit(ConflitException ex, HttpServletRequest requete) {
        return probleme(HttpStatus.CONFLICT, "Conflit", ex.getMessage(), requete.getRequestURI(), null);
    }

    @ExceptionHandler(RessourceExpireeException.class)
    public ResponseEntity<ProblemeApi> expiree(RessourceExpireeException ex, HttpServletRequest requete) {
        return probleme(HttpStatus.GONE, "Lien expiré", ex.getMessage(), requete.getRequestURI(), null);
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ResponseEntity<ProblemeApi> accesRefuse(AccesRefuseException ex, HttpServletRequest requete) {
        return probleme(HttpStatus.FORBIDDEN, "Accès refusé", ex.getMessage(), requete.getRequestURI(), null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemeApi> modificationConcurrente(OptimisticLockingFailureException ex,
                                                               HttpServletRequest requete) {
        return probleme(HttpStatus.CONFLICT, "Modification concurrente",
                "La ressource a été modifiée au même moment par une autre requête. Réessayez.",
                requete.getRequestURI(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemeApi> integrite(DataIntegrityViolationException ex, HttpServletRequest requete) {
        return probleme(HttpStatus.CONFLICT, "Conflit de données",
                "Cette donnée existe déjà ou viole une contrainte d'intégrité.", requete.getRequestURI(), null);
    }

    /** Erreurs de validation (@Valid) : un message par champ invalide. */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError erreur : ex.getBindingResult().getFieldErrors()) {
            erreurs.putIfAbsent(erreur.getField(), erreur.getDefaultMessage());
        }
        String chemin = request instanceof ServletWebRequest servlet ? servlet.getRequest().getRequestURI() : null;
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(new ProblemeApi("about:blank", "Requête invalide", HttpStatus.BAD_REQUEST.value(),
                        "Certains champs sont invalides.", chemin, erreurs));
    }

    private static ResponseEntity<ProblemeApi> probleme(HttpStatus statut, String titre, String detail,
                                                        String chemin, Map<String, String> erreurs) {
        return ResponseEntity.status(statut)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(new ProblemeApi("about:blank", titre, statut.value(), detail, chemin, erreurs));
    }
}
