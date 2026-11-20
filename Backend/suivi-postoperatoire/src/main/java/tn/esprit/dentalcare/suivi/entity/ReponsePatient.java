package tn.esprit.dentalcare.suivi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Réponse soumise par le patient pour un jour spécifique et une question donnée.
 */
@Entity
@Table(name = "reponses_patient")
public class ReponsePatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int jour;

    @Column(nullable = false)
    private int valeur;

    @Column(name = "recue_le", nullable = false)
    private LocalDateTime recueLe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suivi_id", nullable = false)
    private Suivi suivi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionSuivi question;

    public ReponsePatient() {
        this.recueLe = LocalDateTime.now();
    }

    public ReponsePatient(int jour, int valeur, Suivi suivi, QuestionSuivi question) {
        this();
        this.jour = jour;
        this.valeur = valeur;
        this.suivi = suivi;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public LocalDateTime getRecueLe() {
        return recueLe;
    }

    public void setRecueLe(LocalDateTime recueLe) {
        this.recueLe = recueLe;
    }

    public Suivi getSuivi() {
        return suivi;
    }

    public void setSuivi(Suivi suivi) {
        this.suivi = suivi;
    }

    public QuestionSuivi getQuestion() {
        return question;
    }

    public void setQuestion(QuestionSuivi question) {
        this.question = question;
    }
}
