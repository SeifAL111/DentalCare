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
import jakarta.persistence.Version;

import java.time.LocalDateTime;

/**
 * Lien de partage temporaire du dossier.
 * Le lien contient un jeton aléatoire, jamais l'identifiant du dossier.
 * Il s'arrête à la date d'expiration, au nombre maximum de consultations ou à la révocation.
 */
@Entity
@Table(name = "partage_temporaire", indexes = @Index(name = "idx_partage_dossier", columnList = "dossier_id"))
public class PartageTemporaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierMedical dossier;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String token;

    /** Praticien destinataire du partage (identifiant Keycloak). */
    @Column(name = "praticien_id", nullable = false, length = 64)
    private String praticienId;

    @Enumerated(EnumType.STRING)
    @Column(name = "portee", nullable = false, length = 20)
    private PorteePartage portee;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    @Column(name = "expire_le", nullable = false)
    private LocalDateTime expireLe;

    /** Null : nombre de consultations illimité jusqu'à l'expiration. */
    @Column(name = "nb_consultations_max")
    private Integer nbConsultationsMax;

    @Column(name = "nb_consultations", nullable = false)
    private int nbConsultations;

    @Column(name = "revoque", nullable = false)
    private boolean revoque;

    @Column(name = "motif", length = 300)
    private String motif;

    /** Évite que deux consultations simultanées dépassent le nombre maximum. */
    @Version
    private Long version;

    protected PartageTemporaire() {
        // requis par JPA
    }

    public PartageTemporaire(DossierMedical dossier, String token, String praticienId, PorteePartage portee,
                             LocalDateTime creeLe, LocalDateTime expireLe, Integer nbConsultationsMax,
                             String motif) {
        this.dossier = dossier;
        this.token = token;
        this.praticienId = praticienId;
        this.portee = portee;
        this.creeLe = creeLe;
        this.expireLe = expireLe;
        this.nbConsultationsMax = nbConsultationsMax;
        this.motif = motif;
    }

    public boolean estUtilisable(LocalDateTime maintenant) {
        return !revoque
                && maintenant.isBefore(expireLe)
                && (nbConsultationsMax == null || nbConsultations < nbConsultationsMax);
    }

    public void enregistrerConsultation() {
        nbConsultations++;
    }

    public void revoquer() {
        this.revoque = true;
    }

    public Long getId() {
        return id;
    }

    public DossierMedical getDossier() {
        return dossier;
    }

    public String getToken() {
        return token;
    }

    public String getPraticienId() {
        return praticienId;
    }

    public PorteePartage getPortee() {
        return portee;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public LocalDateTime getExpireLe() {
        return expireLe;
    }

    public Integer getNbConsultationsMax() {
        return nbConsultationsMax;
    }

    public int getNbConsultations() {
        return nbConsultations;
    }

    public boolean isRevoque() {
        return revoque;
    }

    public String getMotif() {
        return motif;
    }
}
