package tn.esprit.dentalcare.agendaclinique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Plage horaire hebdomadaire d'un praticien (ex. lundi 08:30 - 12:30).
 * Le praticien n'est connu que par son identifiant Keycloak (praticienId) : aucune table partagée.
 */
@Entity
@Table(name = "disponibilite")
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String praticienId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DayOfWeek jour;

    @Column(nullable = false)
    private LocalTime debut;

    @Column(nullable = false)
    private LocalTime fin;

    /** Vrai si [debutCreneau, finCreneau] tient entièrement dans cette plage. */
    public boolean couvre(LocalTime debutCreneau, LocalTime finCreneau) {
        return !debutCreneau.isBefore(debut) && !finCreneau.isAfter(fin);
    }

    /** Vrai si les deux plages se chevauchent (même praticien, même jour). */
    public boolean chevauche(LocalTime autreDebut, LocalTime autreFin) {
        return autreDebut.isBefore(fin) && autreFin.isAfter(debut);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPraticienId() {
        return praticienId;
    }

    public void setPraticienId(String praticienId) {
        this.praticienId = praticienId;
    }

    public DayOfWeek getJour() {
        return jour;
    }

    public void setJour(DayOfWeek jour) {
        this.jour = jour;
    }

    public LocalTime getDebut() {
        return debut;
    }

    public void setDebut(LocalTime debut) {
        this.debut = debut;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }
}
