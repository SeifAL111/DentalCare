package tn.esprit.dentalcare.suivi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.dentalcare.suivi.entity.QuestionSuivi;

import java.util.List;

@Repository
public interface QuestionSuiviRepository extends JpaRepository<QuestionSuivi, Long> {
    List<QuestionSuivi> findByProtocoleId(Long protocoleId);
}
