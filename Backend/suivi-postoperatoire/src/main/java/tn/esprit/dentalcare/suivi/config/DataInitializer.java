package tn.esprit.dentalcare.suivi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.dentalcare.suivi.entity.Protocole;
import tn.esprit.dentalcare.suivi.entity.QuestionSuivi;
import tn.esprit.dentalcare.suivi.repository.ProtocoleRepository;

import java.util.Arrays;

/**
 * Initialiseur de données de démonstration au démarrage.
 * Insère automatiquement le protocole DEVITALISATION si la base est vide.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ProtocoleRepository protocoleRepository;

    public DataInitializer(ProtocoleRepository protocoleRepository) {
        this.protocoleRepository = protocoleRepository;
    }

    @Override
    public void run(String... args) {
        if (protocoleRepository.count() == 0) {
            log.info("Base de données vide : insertion du protocole de démonstration DEVITALISATION...");

            Protocole devitalisation = new Protocole(
                    "DEVITALISATION",
                    Arrays.asList(1, 3),
                    "Prendre les antalgiques prescrits en cas de douleur. Éviter de mâcher des aliments durs du côté traité pendant 48 heures. Pratiquer un brossage délicat de la zone opérée. En cas de gonflement ou de douleur aiguë persistante, contacter le cabinet dentaire immédiatement."
            );

            QuestionSuivi questionDouleur = new QuestionSuivi(
                    "Quelle est l'intensité de votre douleur de 0 à 10 ?",
                    7
            );

            devitalisation.addQuestion(questionDouleur);
            protocoleRepository.save(devitalisation);

            log.info("Protocole DEVITALISATION inséré avec succès (ID: {}, Seuil alerte: {}).",
                    devitalisation.getId(), questionDouleur.getSeuilAlerte());
        } else {
            log.info("Des protocoles existent déjà en base de données. Pas d'insertion nécessaire.");
        }
    }
}
