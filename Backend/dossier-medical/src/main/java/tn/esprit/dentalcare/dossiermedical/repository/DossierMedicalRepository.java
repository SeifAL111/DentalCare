package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.DossierMedical;

import java.util.Optional;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {

    Optional<DossierMedical> findByIdAndArchiveFalse(Long id);

    Optional<DossierMedical> findByPatientIdAndArchiveFalse(String patientId);

    /** Inclut les dossiers archivés : un patient ne peut jamais avoir deux dossiers. */
    boolean existsByPatientId(String patientId);

    Page<DossierMedical> findByArchiveFalse(Pageable pageable);

    Page<DossierMedical> findByArchiveFalseAndPatientIdContainingIgnoreCase(String patientId, Pageable pageable);
}
