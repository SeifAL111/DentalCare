package tn.esprit.dentalcare.plantraitement.config;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.dentalcare.plantraitement.domain.ActeCatalogue;
import tn.esprit.dentalcare.plantraitement.repository.ActeCatalogueRepository;

@Configuration
public class CatalogueDataSeeder {

    @Bean
    CommandLineRunner seedActes(ActeCatalogueRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.save(acte("DEVIT", "Dévitalisation", 60, "180.000", 14, "ENDODONTIE"));
            repository.save(acte("EMPREINTE", "Empreinte prothétique", 45, "120.000", 7, "PROTHESE"));
            repository.save(acte("POSE_COURONNE", "Pose de couronne", 50, "350.000", 0, "PROTHESE"));
            repository.save(acte("DETARTRAGE", "Détartrage", 30, "60.000", 0, "HYGIENE"));
            repository.save(acte("EXTRACTION", "Extraction dentaire", 40, "90.000", 7, "CHIRURGIE"));
        };
    }

    private ActeCatalogue acte(
            String code,
            String libelle,
            int duree,
            String tarif,
            int delai,
            String competence) {
        ActeCatalogue acte = new ActeCatalogue();
        acte.setCode(code);
        acte.setLibelle(libelle);
        acte.setDureeMinutes(duree);
        acte.setTarif(new BigDecimal(tarif));
        acte.setDelaiCicatrisationJours(delai);
        acte.setCompetenceRequise(competence);
        return acte;
    }
}
