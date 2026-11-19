package tn.esprit.dentalcare.agendaclinique.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.CreneauResponse;
import tn.esprit.dentalcare.agendaclinique.repository.DisponibiliteRepository;
import tn.esprit.dentalcare.agendaclinique.repository.FauteuilRepository;
import tn.esprit.dentalcare.agendaclinique.repository.RendezVousRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;

/**
 * GET /api/creneaux : créneaux compatibles avec un acte.
 * Un créneau exige un praticien disponible et libre, un fauteuil libre, et la durée de l'acte.
 */
@Service
@Transactional(readOnly = true)
public class CreneauService {

    /** Les créneaux proposés commencent tous les quarts d'heure. */
    static final int PAS_MINUTES = 15;
    static final int MAX_RESULTATS = 100;
    static final int MAX_JOURS = 31;

    private final DisponibiliteRepository disponibiliteRepository;
    private final FauteuilRepository fauteuilRepository;
    private final RendezVousRepository rendezVousRepository;
    private final ReservationEngine reservationEngine;

    public CreneauService(
            DisponibiliteRepository disponibiliteRepository,
            FauteuilRepository fauteuilRepository,
            RendezVousRepository rendezVousRepository,
            ReservationEngine reservationEngine) {
        this.disponibiliteRepository = disponibiliteRepository;
        this.fauteuilRepository = fauteuilRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.reservationEngine = reservationEngine;
    }

    public List<CreneauResponse> rechercher(String acteCode, String praticienId, LocalDate date, int jours) {
        if (jours < 1 || jours > MAX_JOURS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "jours doit être compris entre 1 et " + MAX_JOURS);
        }
        int duree = reservationEngine.dureeActe(acteCode);
        String code = acteCode.trim().toUpperCase(Locale.ROOT);
        LocalDate premierJour = date != null ? date : LocalDate.now();
        LocalDateTime maintenant = LocalDateTime.now();

        List<Disponibilite> plages = (praticienId == null || praticienId.isBlank())
                ? disponibiliteRepository.findAllByOrderByPraticienIdAscJourAscDebutAsc()
                : disponibiliteRepository.findByPraticienIdOrderByJourAscDebutAsc(praticienId);
        List<Fauteuil> fauteuils = fauteuilRepository.findByActifTrueOrderByNumeroAsc();
        if (plages.isEmpty() || fauteuils.isEmpty()) {
            return List.of();
        }

        // Tous les rendez-vous confirmés de la période, chargés une seule fois
        LocalDateTime debutPeriode = premierJour.atStartOfDay();
        LocalDateTime finPeriode = premierJour.plusDays(jours).atStartOfDay();
        List<RendezVous> occupes = rendezVousRepository
                .findByStatutAndDebutLessThanAndFinGreaterThan(StatutRdv.CONFIRME, finPeriode, debutPeriode);

        List<CreneauResponse> resultats = new ArrayList<>();
        for (int j = 0; j < jours; j++) {
            LocalDate jour = premierJour.plusDays(j);
            for (Disponibilite plage : plages) {
                if (plage.getJour() != jour.getDayOfWeek()) {
                    continue;
                }
                int minuteDebutPlage = plage.getDebut().toSecondOfDay() / 60;
                int minuteFinPlage = plage.getFin().toSecondOfDay() / 60;
                for (int m = minuteDebutPlage; m + duree <= minuteFinPlage; m += PAS_MINUTES) {
                    LocalDateTime debut = jour.atStartOfDay().plusMinutes(m);
                    LocalDateTime fin = debut.plusMinutes(duree);
                    if (!debut.isAfter(maintenant) || praticienOccupe(occupes, plage.getPraticienId(), debut, fin)) {
                        continue;
                    }
                    Fauteuil libre = premierFauteuilLibre(fauteuils, occupes, debut, fin);
                    if (libre != null) {
                        resultats.add(new CreneauResponse(plage.getPraticienId(), libre.getId(), libre.getNumero(),
                                libre.getSalle(), code, debut, fin, duree));
                    }
                }
            }
        }
        resultats.sort(Comparator.comparing(CreneauResponse::debut).thenComparing(CreneauResponse::praticienId));
        return resultats.size() > MAX_RESULTATS ? resultats.subList(0, MAX_RESULTATS) : resultats;
    }

    private static boolean chevauche(RendezVous r, LocalDateTime debut, LocalDateTime fin) {
        return r.getDebut().isBefore(fin) && r.getFin().isAfter(debut);
    }

    private static boolean praticienOccupe(List<RendezVous> occupes, String praticienId, LocalDateTime debut, LocalDateTime fin) {
        return occupes.stream().anyMatch(r -> r.getPraticienId().equals(praticienId) && chevauche(r, debut, fin));
    }

    private static Fauteuil premierFauteuilLibre(List<Fauteuil> fauteuils, List<RendezVous> occupes,
            LocalDateTime debut, LocalDateTime fin) {
        for (Fauteuil f : fauteuils) {
            boolean pris = occupes.stream().anyMatch(r -> r.getFauteuil().getId().equals(f.getId()) && chevauche(r, debut, fin));
            if (!pris) {
                return f;
            }
        }
        return null;
    }
}
