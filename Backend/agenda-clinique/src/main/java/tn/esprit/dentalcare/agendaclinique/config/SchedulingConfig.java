package tn.esprit.dentalcare.agendaclinique.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Active OffreExpirationJob (expiration des offres de créneau après 15 minutes). */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
