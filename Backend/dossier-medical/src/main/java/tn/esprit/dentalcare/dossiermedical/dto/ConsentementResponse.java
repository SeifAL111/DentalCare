package tn.esprit.dentalcare.dossiermedical.dto;

import tn.esprit.dentalcare.dossiermedical.domain.Consentement;
import tn.esprit.dentalcare.dossiermedical.domain.StatutConsentement;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConsentementResponse(
        Long id,
        Long dossierId,
        TypeActe typeActe,
        String signataire,
        String praticienId,
        String versionDocument,
        String texteInforme,
        LocalDateTime signeLe,
        LocalDate expireLe,
        StatutConsentement statut,
        LocalDateTime revoqueLe,
        String motifRevocation,
        String empreinte) {

    public static ConsentementResponse from(Consentement c) {
        return new ConsentementResponse(
                c.getId(),
                c.getDossier().getId(),
                c.getTypeActe(),
                c.getSignataire(),
                c.getPraticienId(),
                c.getVersionDocument(),
                c.getTexteInforme(),
                c.getSigneLe(),
                c.getExpireLe(),
                c.statutAu(LocalDate.now()),
                c.getRevoqueLe(),
                c.getMotifRevocation(),
                c.getEmpreinte());
    }
}
