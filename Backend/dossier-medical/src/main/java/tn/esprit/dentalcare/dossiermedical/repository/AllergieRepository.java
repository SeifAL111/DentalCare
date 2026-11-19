package tn.esprit.dentalcare.dossiermedical.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.dossiermedical.domain.Allergie;

import java.util.List;
import java.util.Optional;

public interface AllergieRepository extends JpaRepository<Allergie, Long> {

    List<Allergie> findByDossierIdOrderByCreeLeDesc(Long dossierId);

    /** Recherche par identifiant ET dossier : empêche de toucher l'allergie d'un autre patient via l'URL. */
    Optional<Allergie> findByIdAndDossierId(Long id, Long dossierId);

    long countByDossierId(Long dossierId);
}
