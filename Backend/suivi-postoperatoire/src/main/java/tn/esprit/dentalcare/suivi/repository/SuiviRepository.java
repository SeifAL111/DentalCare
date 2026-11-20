package tn.esprit.dentalcare.suivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.suivi.entity.StatutSuivi;
import tn.esprit.dentalcare.suivi.entity.Suivi;

import java.util.List;

@Repository
public interface SuiviRepository extends JpaRepository<Suivi, Long> {
    List<Suivi> findByPatientId(String patientId);
    List<Suivi> findByStatut(StatutSuivi statut);
}
