package tn.esprit.dentalcare.plantraitement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "devis")
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false, unique = true)
    private PlanTraitement plan;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal montantTotal;

    private LocalDate accepteLe;

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

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDate getAccepteLe() {
        return accepteLe;
    }

    public void setAccepteLe(LocalDate accepteLe) {
        this.accepteLe = accepteLe;
    }
}
