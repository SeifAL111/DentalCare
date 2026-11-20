package tn.esprit.dentalcare.suivi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Alerte générée automatiquement lorsque la réponse d'un patient
 * dépasse le seuil fixé par la question du protocole.
 */
@Entity
@Table(name = "alertes")
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String motif;

    @Column(name = "cree_le", nullable = false)
    private LocalDateTime creeLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suivi_id", nullable = false)
    private Suivi suivi;

    public Alerte() {
        this.creeLe = LocalDateTime.now();
    }

    public Alerte(String motif, Suivi suivi) {
        this();
        this.motif = motif;
        this.suivi = suivi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Suivi getSuivi() {
        return suivi;
    }

    public void setSuivi(Suivi suivi) {
        this.suivi = suivi;
    }
}
