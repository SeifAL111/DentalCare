package tn.esprit.dentalcare.laboratoire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.laboratoire.domain.CommandeProthese;
import tn.esprit.dentalcare.laboratoire.domain.StatutProthese;

import java.util.List;

@Repository
public interface CommandeProtheseRepository extends JpaRepository<CommandeProthese, Long> {

    // Permet de filtrer les commandes par laboratoire (consigne du guide équipe)
    List<CommandeProthese> findByLaboratoireId(Long laboratoireId);

    // Permet de filtrer par statut (ex: trouver toutes les commandes en attente)
    List<CommandeProthese> findByStatut(StatutProthese statut);
}