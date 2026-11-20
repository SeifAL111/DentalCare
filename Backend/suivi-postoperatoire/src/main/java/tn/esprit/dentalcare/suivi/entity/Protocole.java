package tn.esprit.dentalcare.suivi.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de protocole post-opératoire rattaché à un type d'acte dentaire.
 */
@Entity
@Table(name = "protocoles")
public class Protocole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acte_code", unique = true, nullable = false)
    private String acteCode;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "protocole_jours", joinColumns = @JoinColumn(name = "protocole_id"))
    @Column(name = "jour")
    private List<Integer> jours = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String consignes;

    @OneToMany(mappedBy = "protocole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionSuivi> questions = new ArrayList<>();

    public Protocole() {
    }

    public Protocole(String acteCode, List<Integer> jours, String consignes) {
        this.acteCode = acteCode;
        this.jours = jours != null ? jours : new ArrayList<>();
        this.consignes = consignes;
    }

    public void addQuestion(QuestionSuivi question) {
        questions.add(question);
        question.setProtocole(this);
    }

    public void removeQuestion(QuestionSuivi question) {
        questions.remove(question);
        question.setProtocole(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActeCode() {
        return acteCode;
    }

    public void setActeCode(String acteCode) {
        this.acteCode = acteCode;
    }

    public List<Integer> getJours() {
        return jours;
    }

    public void setJours(List<Integer> jours) {
        this.jours = jours;
    }

    public String getConsignes() {
        return consignes;
    }

    public void setConsignes(String consignes) {
        this.consignes = consignes;
    }

    public List<QuestionSuivi> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionSuivi> questions) {
        this.questions = questions;
    }
}
