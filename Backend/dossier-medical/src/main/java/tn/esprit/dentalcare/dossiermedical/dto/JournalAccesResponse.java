package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.JournalAcces;
import tn.esprit.dentalcare.dossiermedical.domain.PorteePartage;

import java.time.LocalDateTime;

public record JournalAccesResponse(
        Long id,
        Long partageId,
        String praticienId,
        LocalDateTime accedeLe,
        String adresse,
        PorteePartage portee) {

    public static JournalAccesResponse from(JournalAcces j) {
        return new JournalAccesResponse(
                j.getId(),
                j.getPartageId(),
                j.getPraticienId(),
                j.getAccedeLe(),
                j.getAdresse(),
                j.getPortee());
    }
}
