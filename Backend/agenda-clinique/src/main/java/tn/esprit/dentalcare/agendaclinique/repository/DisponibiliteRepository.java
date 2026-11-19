package tn.esprit.dentalcare.agendaclinique.repository;

import jakarta.persistence.LockModeType;
import java.time.DayOfWeek;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findAllByOrderByPraticienIdAscJourAscDebutAsc();

    List<Disponibilite> findByPraticienIdOrderByJourAscDebutAsc(String praticienId);

    List<Disponibilite> findByPraticienIdAndJour(String praticienId, DayOfWeek jour);

    /**
     * SELECT ... FOR UPDATE sur les plages du praticien ce jour-là :
     * deux réservations pour le même praticien le même jour passent l'une après l'autre.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Disponibilite d where d.praticienId = :praticienId and d.jour = :jour")
    List<Disponibilite> findForUpdate(@Param("praticienId") String praticienId, @Param("jour") DayOfWeek jour);
}
