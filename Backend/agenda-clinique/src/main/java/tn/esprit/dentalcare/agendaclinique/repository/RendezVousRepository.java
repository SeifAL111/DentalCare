package tn.esprit.dentalcare.agendaclinique.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findAllByOrderByDebutAsc();

    List<RendezVous> findByDebutGreaterThanEqualAndDebutLessThanOrderByDebutAsc(LocalDateTime from, LocalDateTime to);

    /** Rendez-vous d'un statut donné qui touchent l'intervalle [from, to[. */
    List<RendezVous> findByStatutAndDebutLessThanAndFinGreaterThan(StatutRdv statut, LocalDateTime to, LocalDateTime from);

    // Chevauchement de [debut, fin[ avec un rendez-vous existant : existant.debut < fin ET existant.fin > debut

    @Query("select count(r) from RendezVous r where r.fauteuil.id = :fauteuilId and r.statut = :statut "
            + "and r.debut < :fin and r.fin > :debut")
    long countChevauchementsFauteuil(@Param("fauteuilId") Long fauteuilId, @Param("statut") StatutRdv statut,
            @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("select count(r) from RendezVous r where r.praticienId = :praticienId and r.statut = :statut "
            + "and r.debut < :fin and r.fin > :debut")
    long countChevauchementsPraticien(@Param("praticienId") String praticienId, @Param("statut") StatutRdv statut,
            @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("select count(r) from RendezVous r where r.patientId = :patientId and r.statut = :statut "
            + "and r.debut < :fin and r.fin > :debut")
    long countChevauchementsPatient(@Param("patientId") String patientId, @Param("statut") StatutRdv statut,
            @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
