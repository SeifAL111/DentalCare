package tn.esprit.dentalcare.agendaclinique.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.dentalcare.agendaclinique.domain.InscriptionAttente;
import tn.esprit.dentalcare.agendaclinique.domain.StatutInscription;

public interface InscriptionAttenteRepository extends JpaRepository<InscriptionAttente, Long> {

    /** File d'attente : premier inscrit, premier servi. */
    List<InscriptionAttente> findByStatutOrderByInscritLeAsc(StatutInscription statut);

    boolean existsByPatientIdAndActeCodeAndStatut(String patientId, String acteCode, StatutInscription statut);
}
