package tn.esprit.dentalcare.laboratoire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.laboratoire.domain.Retouche;

import java.util.List;

@Repository
public interface RetoucheRepository extends JpaRepository<Retouche, Long> {

    // Retrouver les retouches associées à une commande de prothèse spécifique
    List<Retouche> findByCommandeProtheseId(Long commandeId);
}