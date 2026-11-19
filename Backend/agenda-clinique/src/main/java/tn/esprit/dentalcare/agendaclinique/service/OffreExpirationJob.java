package tn.esprit.dentalcare.agendaclinique.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Toutes les minutes : les offres de plus de 15 minutes expirent et le créneau passe au patient suivant. */
@Component
public class OffreExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(OffreExpirationJob.class);

    private final ListeAttenteService listeAttenteService;

    public OffreExpirationJob(ListeAttenteService listeAttenteService) {
        this.listeAttenteService = listeAttenteService;
    }

    @Scheduled(fixedDelayString = "${agenda.offres.verification-ms:60000}",
            initialDelayString = "${agenda.offres.verification-ms:60000}")
    public void verifierExpirations() {
        int expirees = listeAttenteService.expirerOffresEchues();
        if (expirees > 0) {
            log.info("{} offre(s) de créneau expirée(s)", expirees);
        }
    }
}
