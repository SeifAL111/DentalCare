package tn.esprit.dentalcare.plantraitement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "acte_catalogue")
public class ActeCatalogue {

    @Id
    @Column(length = 40)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    /** Duration in minutes. */
    @Column(nullable = false)
    private Integer dureeMinutes;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal tarif;

    /** Minimum healing delay in days before next step. */
    @Column(nullable = false)
    private Integer delaiCicatrisationJours;

    @Column(length = 80)
    private String competenceRequise;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public BigDecimal getTarif() {
        return tarif;
    }

    public void setTarif(BigDecimal tarif) {
        this.tarif = tarif;
    }

    public Integer getDelaiCicatrisationJours() {
        return delaiCicatrisationJours;
    }

    public void setDelaiCicatrisationJours(Integer delaiCicatrisationJours) {
        this.delaiCicatrisationJours = delaiCicatrisationJours;
    }

    public String getCompetenceRequise() {
        return competenceRequise;
    }

    public void setCompetenceRequise(String competenceRequise) {
        this.competenceRequise = competenceRequise;
    }
}
