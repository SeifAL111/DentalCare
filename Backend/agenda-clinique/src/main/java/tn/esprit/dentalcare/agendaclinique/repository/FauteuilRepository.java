package tn.esprit.dentalcare.agendaclinique.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;

public interface FauteuilRepository extends JpaRepository<Fauteuil, Long> {

    boolean existsByNumero(String numero);

    Optional<Fauteuil> findByNumero(String numero);

    List<Fauteuil> findAllByOrderByNumeroAsc();

    List<Fauteuil> findByActifTrueOrderByNumeroAsc();

    /** SELECT ... FOR UPDATE : deux réservations sur le même fauteuil passent l'une après l'autre. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Fauteuil f where f.id = :id")
    Optional<Fauteuil> findByIdForUpdate(@Param("id") Long id);
}
