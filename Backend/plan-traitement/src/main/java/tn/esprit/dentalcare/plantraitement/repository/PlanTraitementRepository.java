package tn.esprit.dentalcare.plantraitement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.plantraitement.domain.PlanTraitement;

public interface PlanTraitementRepository extends JpaRepository<PlanTraitement, Long> {
    List<PlanTraitement> findByPatientIdOrderByCreeLeDesc(String patientId);
}
