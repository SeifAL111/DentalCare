package tn.esprit.dentalcare.suivi.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Format standard de réponse d'erreur renvoyé par l'API REST.
 */
@Schema(description = "Structure d'erreur standard de l'API")
public class ErrorResponse {

    @Schema(description = "Date et heure de l'erreur", example = "2026-11-19T21:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Code de statut HTTP", example = "404")
    private int status;

    @Schema(description = "Message détaillé expliquant l'erreur", example = "Protocole introuvable avec l'identifiant : 1")
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
