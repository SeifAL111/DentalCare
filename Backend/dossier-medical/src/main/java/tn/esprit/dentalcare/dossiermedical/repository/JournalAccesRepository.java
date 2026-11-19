package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.JournalAcces;

import java.util.List;

public interface JournalAccesRepository extends JpaRepository<JournalAcces, Long> {

    List<JournalAcces> findByDossierIdOrderByAccedeLeDesc(Long dossierId);
}
