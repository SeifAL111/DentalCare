package tn.esprit.dentalcare.plantraitement.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.plantraitement.domain.Facture;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByPlanIdOrderByEmiseLeDesc(Long planId);
}
