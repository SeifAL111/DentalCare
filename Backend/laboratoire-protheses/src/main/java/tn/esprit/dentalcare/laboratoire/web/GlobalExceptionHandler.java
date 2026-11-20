package tn.esprit.dentalcare.laboratoire.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleNotFound(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erreur", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleInvalidTransition(IllegalStateException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erreur", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}