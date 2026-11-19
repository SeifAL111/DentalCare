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

@Entity
@Table(name = "allergie", indexes = @Index(name = "idx_allergie_dossier", columnList = "dossier_id"))
public class Allergie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dossier_id", nullable = false)
    private DossierMedical dossier;

    @Column(name = "substance", nullable = false, length = 120)
    private String substance;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 20)
    private CategorieAllergie categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "severite", nullable = false, length = 20)
    private Severite severite;

    /** Description de la réaction observée (ex. « œdème de Quincke en 2019 »). */
    @Column(name = "reaction", length = 500)
    private String reaction;

    @Column(name = "declaree_le")
    private LocalDate declareeLe;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    protected Allergie() {
        // requis par JPA
    }

    public Allergie(DossierMedical dossier) {
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

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public CategorieAllergie getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieAllergie categorie) {
        this.categorie = categorie;
    }

    public Severite getSeverite() {
        return severite;
    }

    public void setSeverite(Severite severite) {
        this.severite = severite;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public LocalDate getDeclareeLe() {
        return declareeLe;
    }

    public void setDeclareeLe(LocalDate declareeLe) {
        this.declareeLe = declareeLe;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }
}
