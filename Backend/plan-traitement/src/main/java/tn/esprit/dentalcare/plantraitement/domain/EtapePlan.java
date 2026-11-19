package tn.esprit.dentalcare.plantraitement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "etape_plan")
public class EtapePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanTraitement plan;

    @Column(nullable = false)
    private Integer ordre;

    @Column(nullable = false, length = 40)
    private String acteCode;

    /** FDI tooth number when applicable. */
    private Integer dent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutEtape statut = StatutEtape.PLANIFIEE;

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

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public Integer getDent() {
        return dent;
    }

    public void setDent(Integer dent) {
        this.dent = dent;
    }

    public StatutEtape getStatut() {
        return statut;
    }

    public void setStatut(StatutEtape statut) {
        this.statut = statut;
    }
}
