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
 * Offre d'un créneau libéré (rendez-vous annulé ou déplacé) à un patient de la liste d'attente.
 * Valable 15 minutes : acceptée, refusée ou expirée, puis le créneau passe au patient suivant.
 */
@Entity
@Table(name = "offre_creneau")
public class OffreCreneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Le rendez-vous annulé ou déplacé dont on offre le créneau. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "rendez_vous_libere_id", nullable = false)
    private RendezVous rendezVousLibere;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inscription_id", nullable = false)
    private InscriptionAttente inscription;

    @Column(nullable = false)
    private LocalDateTime creeLe = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expireLe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutOffre statut = StatutOffre.EN_ATTENTE;

    /** Rendez-vous créé quand l'offre est acceptée. */
    private Long rendezVousCreeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RendezVous getRendezVousLibere() {
        return rendezVousLibere;
    }

    public void setRendezVousLibere(RendezVous rendezVousLibere) {
        this.rendezVousLibere = rendezVousLibere;
    }

    public InscriptionAttente getInscription() {
        return inscription;
    }

    public void setInscription(InscriptionAttente inscription) {
        this.inscription = inscription;
    }

    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(LocalDateTime creeLe) {
        this.creeLe = creeLe;
    }

    public LocalDateTime getExpireLe() {
        return expireLe;
    }

    public void setExpireLe(LocalDateTime expireLe) {
        this.expireLe = expireLe;
    }

    public StatutOffre getStatut() {
        return statut;
    }

    public void setStatut(StatutOffre statut) {
        this.statut = statut;
    }

    public Long getRendezVousCreeId() {
        return rendezVousCreeId;
    }

    public void setRendezVousCreeId(Long rendezVousCreeId) {
        this.rendezVousCreeId = rendezVousCreeId;
    }
}
