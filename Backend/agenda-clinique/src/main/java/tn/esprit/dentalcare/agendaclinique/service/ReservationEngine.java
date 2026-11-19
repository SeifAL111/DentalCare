package tn.esprit.dentalcare.agendaclinique.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tn.esprit.dentalcare.agendaclinique.catalogue.CatalogueActes;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;
import tn.esprit.dentalcare.agendaclinique.repository.DisponibiliteRepository;
import tn.esprit.dentalcare.agendaclinique.repository.FauteuilRepository;
import tn.esprit.dentalcare.agendaclinique.repository.RendezVousRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;

/**
 * Cœur de la réservation : règle « pas de double réservation » par verrouillage transactionnel.
 *
 * <p>Ordre des verrous, toujours le même (évite les interblocages) :
 * 1) plages du praticien ce jour-là, 2) fauteuil. Puis contrôle des chevauchements et insertion.
 * Deux demandes concurrentes sur le même praticien ou le même fauteuil passent donc l'une après l'autre,
 * et la seconde voit le rendez-vous de la première : 409.
 *
 * <p>Volontairement SANS @Transactional : toujours appelé depuis une méthode @Transactional d'un service
 * (les verrous vivent jusqu'au commit de cette transaction). Ainsi un 409 attrapé par l'appelant
 * (recherche d'un créneau d'urgence) ne marque pas la transaction « rollback-only ».
 */
@Component
public class ReservationEngine {

    private final RendezVousRepository rendezVousRepository;
    private final FauteuilRepository fauteuilRepository;
    private final DisponibiliteRepository disponibiliteRepository;
    private final CatalogueActes catalogueActes;

    public ReservationEngine(
            RendezVousRepository rendezVousRepository,
            FauteuilRepository fauteuilRepository,
            DisponibiliteRepository disponibiliteRepository,
            CatalogueActes catalogueActes) {
        this.rendezVousRepository = rendezVousRepository;
        this.fauteuilRepository = fauteuilRepository;
        this.disponibiliteRepository = disponibiliteRepository;
        this.catalogueActes = catalogueActes;
    }

    /** Durée de l'acte en minutes, ou 400 si l'acte n'existe pas dans le catalogue. */
    public int dureeActe(String acteCode) {
        return catalogueActes.dureeMinutes(acteCode)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Acte inconnu du catalogue : " + acteCode));
    }

    public RendezVous reserver(DemandeReservation demande) {
        int duree = dureeActe(demande.acteCode());
        LocalDateTime debut = demande.debut();
        LocalDateTime fin = debut.plusMinutes(duree);

        if (!debut.isAfter(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Le créneau doit être dans le futur");
        }
        if (!fin.toLocalDate().equals(debut.toLocalDate())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Le rendez-vous doit se terminer le même jour");
        }

        // 1) Verrou praticien : ses plages du jour (et contrôle qu'il travaille sur ce créneau)
        List<Disponibilite> plages = disponibiliteRepository.findForUpdate(demande.praticienId(), debut.getDayOfWeek());
        boolean couvert = plages.stream().anyMatch(p -> p.couvre(debut.toLocalTime(), fin.toLocalTime()));
        if (!couvert) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Le praticien " + demande.praticienId()
                    + " n'est pas disponible le " + debut.getDayOfWeek() + " de " + debut.toLocalTime()
                    + " à " + fin.toLocalTime());
        }

        // 2) Verrou fauteuil
        Fauteuil fauteuil = fauteuilRepository.findByIdForUpdate(demande.fauteuilId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fauteuil introuvable : " + demande.fauteuilId()));
        if (!fauteuil.isActif()) {
            throw new ApiException(HttpStatus.CONFLICT, "Le fauteuil " + fauteuil.getNumero() + " est hors service");
        }

        // 3) Chevauchements (seuls les rendez-vous CONFIRME occupent un créneau)
        if (rendezVousRepository.countChevauchementsFauteuil(fauteuil.getId(), StatutRdv.CONFIRME, debut, fin) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Le fauteuil " + fauteuil.getNumero() + " est déjà pris sur ce créneau");
        }
        if (rendezVousRepository.countChevauchementsPraticien(demande.praticienId(), StatutRdv.CONFIRME, debut, fin) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Le praticien " + demande.praticienId() + " a déjà un rendez-vous sur ce créneau");
        }
        if (rendezVousRepository.countChevauchementsPatient(demande.patientId(), StatutRdv.CONFIRME, debut, fin) > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "Le patient " + demande.patientId() + " a déjà un rendez-vous sur ce créneau");
        }

        RendezVous rdv = new RendezVous();
        rdv.setPatientId(demande.patientId());
        rdv.setPraticienId(demande.praticienId());
        rdv.setFauteuil(fauteuil);
        rdv.setActeCode(demande.acteCode().trim().toUpperCase(Locale.ROOT));
        rdv.setDebut(debut);
        rdv.setFin(fin);
        rdv.setDureeMinutes(duree);
        rdv.setStatut(StatutRdv.CONFIRME);
        rdv.setUrgence(demande.urgence());
        rdv.setEtapePlanId(demande.etapePlanId());
        rdv.setMotif(demande.motif());
        return rendezVousRepository.save(rdv);
    }

    /** Le créneau [debut, fin[ est-il libre pour ce praticien ET ce fauteuil ? */
    public boolean creneauLibre(String praticienId, Long fauteuilId, LocalDateTime debut, LocalDateTime fin) {
        return rendezVousRepository.countChevauchementsFauteuil(fauteuilId, StatutRdv.CONFIRME, debut, fin) == 0
                && rendezVousRepository.countChevauchementsPraticien(praticienId, StatutRdv.CONFIRME, debut, fin) == 0;
    }
}
