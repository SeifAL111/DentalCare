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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Antécédent médical, chirurgical, familial, habitude de vie ou traitement en cours.
 * Le champ facteurRisque, optionnel, rend l'antécédent exploitable par les règles métier.
 */
@Entity
@Table(name = "antecedent", indexes = @Index(name = "idx_antecedent_dossier", columnList = "dossier_id"))
public class Antecedent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierMedical dossier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_antecedent", nullable = false, length = 30)
    private TypeAntecedent type;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "facteur_risque", length = 40)
    private FacteurRisque facteurRisque;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    /** Un traitement arrêté reste dans l'historique mais ne compte plus comme facteur de risque. */
    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    protected Antecedent() {
        // requis par JPA
    }

    public Antecedent(DossierMedical dossier) {
        this.dossier = dossier;
    }

    @PrePersist
    void avantCreation() {
        creeLe = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public DossierMedical getDossier() {
        return dossier;
    }

    public TypeAntecedent getType() {
        return type;
    }

    public void setType(TypeAntecedent type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FacteurRisque getFacteurRisque() {
        return facteurRisque;
    }

    public void setFacteurRisque(FacteurRisque facteurRisque) {
        this.facteurRisque = facteurRisque;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }
}
