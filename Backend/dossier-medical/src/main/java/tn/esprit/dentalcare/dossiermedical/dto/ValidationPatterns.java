package tn.esprit.dentalcare.dossiermedical.dto;

/**
 * Expressions régulières partagées par les DTO.
 */
public final class ValidationPatterns {

    public static final String GROUPE_SANGUIN = "^(A|B|AB|O)[+-]$";
    public static final String TELEPHONE = "^\\+?[0-9 .-]{6,20}$";

    private ValidationPatterns() {
    }
}
