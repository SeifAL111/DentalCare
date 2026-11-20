package tn.esprit.dentalcare.suivi.entity;

import jakarta.persistence.*;

/**
 * Question associée à un protocole post-opératoire.
 * Si la réponse du patient est >= seuilAlerte, une alerte est levée.
 */
@Entity
@Table(name = "questions_suivi")
public class QuestionSuivi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private int seuilAlerte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocole_id", nullable = false)
    private Protocole protocole;

    public QuestionSuivi() {
    }

    public QuestionSuivi(String libelle, int seuilAlerte) {
        this.libelle = libelle;
        this.seuilAlerte = seuilAlerte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public Protocole getProtocole() {
        return protocole;
    }

    public void setProtocole(Protocole protocole) {
        this.protocole = protocole;
    }
}
