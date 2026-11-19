package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.Consentement;
import tn.esprit.dentalcare.dossiermedical.domain.TypeActe;

import java.util.List;
import java.util.Optional;

public interface ConsentementRepository extends JpaRepository<Consentement, Long> {

    List<Consentement> findByDossierIdOrderBySigneLeDesc(Long dossierId);

    List<Consentement> findByDossierIdAndTypeActeOrderBySigneLeDesc(Long dossierId, TypeActe typeActe);

    Optional<Consentement> findByIdAndDossierId(Long id, Long dossierId);
}
