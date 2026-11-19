package tn.esprit.dentalcare.plantraitement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.plantraitement.domain.ActeCatalogue;

public interface ActeCatalogueRepository extends JpaRepository<ActeCatalogue, String> {
}
