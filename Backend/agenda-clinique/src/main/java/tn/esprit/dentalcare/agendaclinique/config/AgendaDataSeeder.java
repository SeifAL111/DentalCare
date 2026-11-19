package tn.esprit.dentalcare.agendaclinique.config;

import java.time.DayOfWeek;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;
import tn.esprit.dentalcare.agendaclinique.repository.DisponibiliteRepository;
import tn.esprit.dentalcare.agendaclinique.repository.FauteuilRepository;

/**
 * Données de démonstration (base H2 en mémoire, recréée à chaque démarrage) :
 * 3 fauteuils et les horaires de 2 praticiens. Données fictives.
 */
@Configuration
public class AgendaDataSeeder {

    @Bean
    CommandLineRunner seedAgenda(FauteuilRepository fauteuils, DisponibiliteRepository disponibilites) {
        return args -> {
            if (fauteuils.count() == 0) {
                fauteuils.save(fauteuil("F1", "Salle 1"));
                fauteuils.save(fauteuil("F2", "Salle 1"));
                fauteuils.save(fauteuil("F3", "Salle 2"));
            }
            if (disponibilites.count() == 0) {
                // PRAT-001 : du lundi au vendredi, 08:30-12:30 et 14:00-18:00
                for (DayOfWeek jour : new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY}) {
                    disponibilites.save(plage("PRAT-001", jour, "08:30", "12:30"));
                    disponibilites.save(plage("PRAT-001", jour, "14:00", "18:00"));
                }
                // PRAT-002 : du lundi au samedi, 09:00-13:00
                for (DayOfWeek jour : new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY}) {
                    disponibilites.save(plage("PRAT-002", jour, "09:00", "13:00"));
                }
            }
        };
    }

    private static Fauteuil fauteuil(String numero, String salle) {
        Fauteuil f = new Fauteuil();
        f.setNumero(numero);
        f.setSalle(salle);
        f.setActif(true);
        return f;
    }

    private static Disponibilite plage(String praticienId, DayOfWeek jour, String debut, String fin) {
        Disponibilite d = new Disponibilite();
        d.setPraticienId(praticienId);
        d.setJour(jour);
        d.setDebut(LocalTime.parse(debut));
        d.setFin(LocalTime.parse(fin));
        return d;
    }
}
