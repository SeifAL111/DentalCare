package tn.esprit.dentalcare.agendaclinique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Rendez-vous : un patient, un praticien, un fauteuil, un acte, un créneau [debut, fin[.
 * Cycle de vie : CONFIRME -> DEPLACE | ANNULE | TERMINE | ABSENT.
 * patientId, praticienId et etapePlanId sont des identifiants d'autres contextes (jamais leurs tables).
 */
@Entity
@Table(name = "rendez_vous")
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String patientId;

    @Column(nullable = false, length = 80)
    private String praticienId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fauteuil_id", nullable = false)
    private Fauteuil fauteuil;

    /** Code de l'acte du catalogue (Plan de Traitement), ex. DEVIT. */
    @Column(nullable = false, length = 40)
    private String acteCode;

    @Column(nullable = false)
    private LocalDateTime debut;

    /** debut + duree : stockée pour détecter les chevauchements en une requête. */
    @Column(nullable = false)
    private LocalDateTime fin;

    @Column(nullable = false)
    private int dureeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRdv statut = StatutRdv.CONFIRME;

    /** Créneau d'urgence (complication signalée par le Suivi post-opératoire). */
    @Column(nullable = false)
    private boolean urgence;

    /** Étape du plan de traitement concernée (renseignée par la Saga en S7). */
    private Long etapePlanId;

    @Column(length = 255)
    private String motif;

    @Column(nullable = false)
    private LocalDateTime creeLe = LocalDateTime.now();

    private LocalDateTime misAJourLe = LocalDateTime.now();

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

    public String getPraticienId() {
        return praticienId;
    }

    public void setPraticienId(String praticienId) {
        this.praticienId = praticienId;
    }

    public Fauteuil getFauteuil() {
        return fauteuil;
    }

    public void setFauteuil(Fauteuil fauteuil) {
        this.fauteuil = fauteuil;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public StatutRdv getStatut() {
        return statut;
    }

    public void setStatut(StatutRdv statut) {
        this.statut = statut;
    }

    public boolean isUrgence() {
        return urgence;
    }

    public void setUrgence(boolean urgence) {
        this.urgence = urgence;
    }

    public Long getEtapePlanId() {
        return etapePlanId;
    }

    public void setEtapePlanId(Long etapePlanId) {
        this.etapePlanId = etapePlanId;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
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
}
