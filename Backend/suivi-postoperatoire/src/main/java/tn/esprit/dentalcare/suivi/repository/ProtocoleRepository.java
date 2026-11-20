package tn.esprit.dentalcare.suivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.suivi.entity.Protocole;

import java.util.Optional;

@Repository
public interface ProtocoleRepository extends JpaRepository<Protocole, Long> {
    boolean existsByActeCode(String acteCode);
    Optional<Protocole> findByActeCode(String acteCode);
}
