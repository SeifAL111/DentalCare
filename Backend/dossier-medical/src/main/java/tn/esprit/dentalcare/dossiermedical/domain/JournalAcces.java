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
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * Trace d'un accès au dossier via un lien de partage.
 * On ne fait qu'ajouter des lignes : pas de setters, et @Immutable empêche Hibernate de les modifier.
 */
@Entity
@Immutable
@Table(name = "journal_acces", indexes = @Index(name = "idx_journal_dossier", columnList = "dossier_id"))
public class JournalAcces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierMedical dossier;

    @Column(name = "partage_id")
    private Long partageId;

    @Column(name = "praticien_id", length = 64)
    private String praticienId;

    @Column(name = "accede_le", nullable = false)
    private LocalDateTime accedeLe;

    @Column(name = "adresse", length = 64)
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(name = "portee", length = 20)
    private PorteePartage portee;

    protected JournalAcces() {
        // requis par JPA
    }

    public JournalAcces(DossierMedical dossier, Long partageId, String praticienId, String adresse,
                        PorteePartage portee) {
        this.dossier = dossier;
        this.partageId = partageId;
        this.praticienId = praticienId;
        this.adresse = adresse;
        this.portee = portee;
        this.accedeLe = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public DossierMedical getDossier() {
        return dossier;
    }

    public Long getPartageId() {
        return partageId;
    }

    public String getPraticienId() {
        return praticienId;
    }

    public LocalDateTime getAccedeLe() {
        return accedeLe;
    }

    public String getAdresse() {
        return adresse;
    }

    public PorteePartage getPortee() {
        return portee;
    }
}
