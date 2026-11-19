package tn.esprit.dentalcare.plantraitement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "echeance")
public class Echeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanTraitement plan;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDate dateLimite;

    @Column(nullable = false)
    private boolean reglee = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanTraitement getPlan() {
        return plan;
    }

    public void setPlan(PlanTraitement plan) {
        this.plan = plan;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateLimite() {
        return dateLimite;
    }

    public void setDateLimite(LocalDate dateLimite) {
        this.dateLimite = dateLimite;
    }

    public boolean isReglee() {
        return reglee;
    }

    public void setReglee(boolean reglee) {
        this.reglee = reglee;
    }
}
