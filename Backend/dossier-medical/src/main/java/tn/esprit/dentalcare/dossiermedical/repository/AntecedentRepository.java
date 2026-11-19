package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.Antecedent;

import java.util.List;
import java.util.Optional;

public interface AntecedentRepository extends JpaRepository<Antecedent, Long> {

    List<Antecedent> findByDossierIdOrderByCreeLeDesc(Long dossierId);

    List<Antecedent> findByDossierIdAndActifTrue(Long dossierId);

    Optional<Antecedent> findByIdAndDossierId(Long id, Long dossierId);

    long countByDossierId(Long dossierId);
}
