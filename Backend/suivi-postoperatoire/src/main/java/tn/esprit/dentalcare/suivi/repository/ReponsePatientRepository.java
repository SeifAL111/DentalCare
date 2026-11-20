package tn.esprit.dentalcare.suivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.suivi.entity.ReponsePatient;

import java.util.List;

@Repository
public interface ReponsePatientRepository extends JpaRepository<ReponsePatient, Long> {
    boolean existsBySuiviIdAndJourAndQuestionId(Long suiviId, int jour, Long questionId);
    List<ReponsePatient> findBySuiviId(Long suiviId);
}
