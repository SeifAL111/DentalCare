package tn.esprit.dentalcare.suivi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant le suivi post-opératoire actif d'un patient pour un acte dentaire.
 */
@Entity
@Table(name = "suivis")
public class Suivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "acte_id", nullable = false)
    private Long acteId;

    @Column(nullable = false)
    private boolean renforce = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSuivi statut = StatutSuivi.ACTIF;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocole_id", nullable = false)
    private Protocole protocole;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @OneToMany(mappedBy = "suivi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponsePatient> reponses = new ArrayList<>();

    @OneToMany(mappedBy = "suivi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alerte> alertes = new ArrayList<>();

    public Suivi() {
        this.dateOuverture = LocalDateTime.now();
        this.statut = StatutSuivi.ACTIF;
        this.renforce = false;
    }

    public Suivi(String patientId, Long acteId, Protocole protocole) {
        this();
        this.patientId = patientId;
        this.acteId = acteId;
        this.protocole = protocole;
    }

    public void addAlerte(Alerte alerte) {
        alertes.add(alerte);
        alerte.setSuivi(this);
    }

    public void addReponse(ReponsePatient reponse) {
        reponses.add(reponse);
        reponse.setSuivi(this);
    }

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

    public Long getActeId() {
        return acteId;
    }

    public void setActeId(Long acteId) {
        this.acteId = acteId;
    }

    public boolean isRenforce() {
        return renforce;
    }

    public void setRenforce(boolean renforce) {
        this.renforce = renforce;
    }

    public StatutSuivi getStatut() {
        return statut;
    }

    public void setStatut(StatutSuivi statut) {
        this.statut = statut;
    }

    public Protocole getProtocole() {
        return protocole;
    }

    public void setProtocole(Protocole protocole) {
        this.protocole = protocole;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(LocalDateTime dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public List<ReponsePatient> getReponses() {
        return reponses;
    }

    public void setReponses(List<ReponsePatient> reponses) {
        this.reponses = reponses;
    }

    public List<Alerte> getAlertes() {
        return alertes;
    }

    public void setAlertes(List<Alerte> alertes) {
        this.alertes = alertes;
    }
}
