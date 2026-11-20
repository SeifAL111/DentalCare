package tn.esprit.dentalcare.laboratoire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.laboratoire.domain.Laboratoire;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {
}