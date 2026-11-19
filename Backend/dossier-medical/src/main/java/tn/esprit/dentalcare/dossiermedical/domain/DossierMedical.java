package tn.esprit.dentalcare.dossiermedical.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Racine du contexte : un seul dossier par patient.
 * Le patient lui-même vit dans Keycloak ; on ne garde que son identifiant.
 */
@Entity
@Table(name = "dossier_medical",
        uniqueConstraints = @UniqueConstraint(name = "uk_dossier_patient", columnNames = "patient_id"))
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false, length = 64)
    private String patientId;

    @Column(name = "cree_le", nullable = false)
    private LocalDate creeLe;

    @Column(name = "mis_a_jour_le")
    private LocalDateTime misAJourLe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_allergies", nullable = false, length = 20)
    private StatutAllergies statutAllergies = StatutAllergies.NON_RENSEIGNE;

    @Column(name = "groupe_sanguin", length = 3)
    private String groupeSanguin;

    @Column(name = "medecin_traitant", length = 120)
    private String medecinTraitant;

    @Column(name = "contact_urgence_nom", length = 120)
    private String contactUrgenceNom;

    @Column(name = "contact_urgence_telephone", length = 20)
    private String contactUrgenceTelephone;

    @Column(name = "observations", length = 2000)
    private String observations;

    /** Un dossier médical s'archive, il ne se supprime pas. */
    @Column(name = "archive", nullable = false)
    private boolean archive;

    @Column(name = "archive_le")
    private LocalDateTime archiveLe;

    /** Verrou optimiste : deux praticiens ne peuvent pas s'écraser mutuellement. */
    @Version
    private Long version;

    protected DossierMedical() {
        // requis par JPA
    }

    public DossierMedical(String patientId) {
        this.patientId = patientId;
    }

    @PrePersist
    void avantCreation() {
        if (creeLe == null) {
            creeLe = LocalDate.now();
        }
        misAJourLe = LocalDateTime.now();
    }

    @PreUpdate
    void avantMiseAJour() {
        misAJourLe = LocalDateTime.now();
    }

    public void archiver() {
        this.archive = true;
        this.archiveLe = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDate getCreeLe() {
        return creeLe;
    }

    public void setCreeLe(LocalDate creeLe) {
        this.creeLe = creeLe;
    }

    public LocalDateTime getMisAJourLe() {
        return misAJourLe;
    }

    public StatutAllergies getStatutAllergies() {
        return statutAllergies;
    }

    public void setStatutAllergies(StatutAllergies statutAllergies) {
        this.statutAllergies = statutAllergies;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getMedecinTraitant() {
        return medecinTraitant;
    }

    public void setMedecinTraitant(String medecinTraitant) {
        this.medecinTraitant = medecinTraitant;
    }

    public String getContactUrgenceNom() {
        return contactUrgenceNom;
    }

    public void setContactUrgenceNom(String contactUrgenceNom) {
        this.contactUrgenceNom = contactUrgenceNom;
    }

    public String getContactUrgenceTelephone() {
        return contactUrgenceTelephone;
    }

    public void setContactUrgenceTelephone(String contactUrgenceTelephone) {
        this.contactUrgenceTelephone = contactUrgenceTelephone;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public boolean isArchive() {
        return archive;
    }

    public LocalDateTime getArchiveLe() {
        return archiveLe;
    }

    public Long getVersion() {
        return version;
    }
}
