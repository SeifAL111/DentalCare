package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.PartageTemporaire;
import tn.esprit.dentalcare.dossiermedical.domain.PorteePartage;

import java.time.LocalDateTime;

public record PartageResponse(
        Long id,
        String token,
        String lien,
        String praticienId,
        PorteePartage portee,
        String motif,
        LocalDateTime creeLe,
        LocalDateTime expireLe,
        int nbConsultations,
        Integer nbConsultationsMax,
        boolean revoque,
        boolean utilisable) {

    /** Chemin relatif, servi derrière la Gateway sous /api/dossiers/**. */
    public static final String CHEMIN_CONSULTATION = "/api/dossiers/partages/";

    public static PartageResponse from(PartageTemporaire p) {
        return new PartageResponse(
                p.getId(),
                p.getToken(),
                CHEMIN_CONSULTATION + p.getToken(),
                p.getPraticienId(),
                p.getPortee(),
                p.getMotif(),
                p.getCreeLe(),
                p.getExpireLe(),
                p.getNbConsultations(),
                p.getNbConsultationsMax(),
                p.isRevoque(),
                p.estUtilisable(LocalDateTime.now()));
    }
}
