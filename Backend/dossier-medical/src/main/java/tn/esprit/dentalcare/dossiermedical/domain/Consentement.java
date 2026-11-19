package tn.esprit.dentalcare.dossiermedical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Consentement éclairé signé pour un type d'acte.
 * Un document signé ne se modifie pas : il n'a pas de setters, il peut seulement être révoqué.
 * L'empreinte SHA-256 permet de prouver que le texte n'a pas changé après la signature.
 */
@Entity
@Table(name = "consentement", indexes = @Index(name = "idx_consentement_dossier", columnList = "dossier_id"))
public class Consentement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierMedical dossier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_acte", nullable = false, length = 30)
    private TypeActe typeActe;

    /** Nom du patient ou de son représentant légal. */
    @Column(name = "signataire", nullable = false, length = 120)
    private String signataire;

    @Column(name = "praticien_id", nullable = false, length = 64)
    private String praticienId;

    @Column(name = "version_document", length = 20)
    private String versionDocument;

    @Column(name = "texte_informe", nullable = false, length = 5000)
    private String texteInforme;

    @Column(name = "signe_le", nullable = false)
    private LocalDateTime signeLe;

    @Column(name = "expire_le", nullable = false)
    private LocalDate expireLe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutConsentement statut;

    @Column(name = "revoque_le")
    private LocalDateTime revoqueLe;

    @Column(name = "motif_revocation", length = 500)
    private String motifRevocation;

    @Column(name = "empreinte", nullable = false, length = 64)
    private String empreinte;

    protected Consentement() {
        // requis par JPA
    }

    public Consentement(DossierMedical dossier, TypeActe typeActe, String signataire, String praticienId,
                        String versionDocument, String texteInforme, LocalDateTime signeLe,
                        LocalDate expireLe, String empreinte) {
        this.dossier = dossier;
        this.typeActe = typeActe;
        this.signataire = signataire;
        this.praticienId = praticienId;
        this.versionDocument = versionDocument;
        this.texteInforme = texteInforme;
        this.signeLe = signeLe;
        this.expireLe = expireLe;
        this.empreinte = empreinte;
        this.statut = StatutConsentement.SIGNE;
    }

    /** Statut réel à une date : un consentement signé mais dépassé est EXPIRE. */
    public StatutConsentement statutAu(LocalDate date) {
        if (statut == StatutConsentement.REVOQUE) {
            return StatutConsentement.REVOQUE;
        }
        return expireLe.isBefore(date) ? StatutConsentement.EXPIRE : StatutConsentement.SIGNE;
    }

    public boolean estValideAu(LocalDate date) {
        return statutAu(date) == StatutConsentement.SIGNE;
    }

    public void revoquer(String motif) {
        this.statut = StatutConsentement.REVOQUE;
        this.revoqueLe = LocalDateTime.now();
        this.motifRevocation = motif;
    }

    public Long getId() {
        return id;
    }

    public DossierMedical getDossier() {
        return dossier;
    }

    public TypeActe getTypeActe() {
        return typeActe;
    }

    public String getSignataire() {
        return signataire;
    }

    public String getPraticienId() {
        return praticienId;
    }

    public String getVersionDocument() {
        return versionDocument;
    }

    public String getTexteInforme() {
        return texteInforme;
    }

    public LocalDateTime getSigneLe() {
        return signeLe;
    }

    public LocalDate getExpireLe() {
        return expireLe;
    }

    public StatutConsentement getStatut() {
        return statut;
    }

    public LocalDateTime getRevoqueLe() {
        return revoqueLe;
    }

    public String getMotifRevocation() {
        return motifRevocation;
    }

    public String getEmpreinte() {
        return empreinte;
    }
}
