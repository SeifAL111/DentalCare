package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.PartageTemporaire;

import java.util.List;
import java.util.Optional;

public interface PartageTemporaireRepository extends JpaRepository<PartageTemporaire, Long> {

    List<PartageTemporaire> findByDossierIdOrderByCreeLeDesc(Long dossierId);

    Optional<PartageTemporaire> findByIdAndDossierId(Long id, Long dossierId);

    Optional<PartageTemporaire> findByToken(String token);
}
