package tn.esprit.dentalcare.plantraitement.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plan_traitement")
public class PlanTraitement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String patientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutPlan statut = StatutPlan.BROUILLON;

    @Column(nullable = false)
    private LocalDateTime creeLe = LocalDateTime.now();

    private LocalDateTime misAJourLe = LocalDateTime.now();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<EtapePlan> etapes = new ArrayList<>();

    @OneToOne(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Devis devis;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateLimite ASC")
    private List<Echeance> echeances = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public StatutPlan getStatut() {
        return statut;
    }

    public void setStatut(StatutPlan statut) {
        this.statut = statut;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(LocalDateTime creeLe) {
        this.creeLe = creeLe;
    }

    public LocalDateTime getMisAJourLe() {
        return misAJourLe;
    }

    public void setMisAJourLe(LocalDateTime misAJourLe) {
        this.misAJourLe = misAJourLe;
    }

    public List<EtapePlan> getEtapes() {
        return etapes;
    }

    public void setEtapes(List<EtapePlan> etapes) {
        this.etapes = etapes;
    }

    public Devis getDevis() {
        return devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
    }

    public List<Echeance> getEcheances() {
        return echeances;
    }

    public void setEcheances(List<Echeance> echeances) {
        this.echeances = echeances;
    }

    public void addEtape(EtapePlan etape) {
        etapes.add(etape);
        etape.setPlan(this);
    }

    public void addEcheance(Echeance echeance) {
        echeances.add(echeance);
        echeance.setPlan(this);
    }
}
