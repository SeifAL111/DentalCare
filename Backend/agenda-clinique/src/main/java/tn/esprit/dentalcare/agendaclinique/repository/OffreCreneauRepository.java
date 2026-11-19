package tn.esprit.dentalcare.agendaclinique.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.agendaclinique.domain.OffreCreneau;
import tn.esprit.dentalcare.agendaclinique.domain.StatutOffre;

public interface OffreCreneauRepository extends JpaRepository<OffreCreneau, Long> {

    List<OffreCreneau> findAllByOrderByCreeLeDesc();

    List<OffreCreneau> findByStatutOrderByCreeLeDesc(StatutOffre statut);

    List<OffreCreneau> findByInscription_PatientIdOrderByCreeLeDesc(String patientId);

    List<OffreCreneau> findByRendezVousLibere_Id(Long rendezVousId);

    List<OffreCreneau> findByInscription_IdAndStatut(Long inscriptionId, StatutOffre statut);

    boolean existsByInscription_IdAndStatut(Long inscriptionId, StatutOffre statut);

    /** Offres encore en attente dont les 15 minutes sont écoulées. */
    List<OffreCreneau> findByStatutAndExpireLeBefore(StatutOffre statut, LocalDateTime now);
}
