package tn.esprit.dentalcare.dossiermedical.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Corps d'erreur au format RFC 9457 (« Problem Details for HTTP APIs ») :
 * type, title, status, detail, instance, plus « erreurs » pour la validation (un message par champ).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemeApi(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Map<String, String> erreurs) {
}
