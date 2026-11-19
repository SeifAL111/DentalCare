package tn.esprit.dentalcare.agendaclinique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** Inscription d'un patient sur la liste d'attente pour un acte (servie dans l'ordre d'inscription). */
@Entity
@Table(name = "inscription_attente")
public class InscriptionAttente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String patientId;

    @Column(nullable = false, length = 40)
    private String acteCode;

    /** Praticien souhaité ; null = n'importe quel praticien. */
    @Column(length = 80)
    private String praticienId;

    @Column(nullable = false)
    private LocalDateTime inscritLe = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutInscription statut = StatutInscription.EN_ATTENTE;

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

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public String getPraticienId() {
        return praticienId;
    }

    public void setPraticienId(String praticienId) {
        this.praticienId = praticienId;
    }

    public LocalDateTime getInscritLe() {
        return inscritLe;
    }

    public void setInscritLe(LocalDateTime inscritLe) {
        this.inscritLe = inscritLe;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }
}
