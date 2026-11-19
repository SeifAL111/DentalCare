package tn.esprit.dentalcare.agendaclinique.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.CreneauResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.DeplacementRequest;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.RendezVousResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.ReservationRequest;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.UrgenceRequest;
import tn.esprit.dentalcare.agendaclinique.repository.RendezVousRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;

@Service
@Transactional
public class RendezVousService {

    private static final Logger log = LoggerFactory.getLogger(RendezVousService.class);

    /** Recherche d'un créneau d'urgence : aujourd'hui + 2 jours. */
    static final int JOURS_URGENCE = 3;
    static final String ACTE_URGENCE = "URGENCE";

    private final RendezVousRepository rendezVousRepository;
    private final ReservationEngine reservationEngine;
    private final CreneauService creneauService;
    private final ListeAttenteService listeAttenteService;

    public RendezVousService(
            RendezVousRepository rendezVousRepository,
            ReservationEngine reservationEngine,
            CreneauService creneauService,
            ListeAttenteService listeAttenteService) {
        this.rendezVousRepository = rendezVousRepository;
        this.reservationEngine = reservationEngine;
        this.creneauService = creneauService;
        this.listeAttenteService = listeAttenteService;
    }

    /** POST /api/rdv — 201, 400 (hors disponibilité, acte inconnu), 404 (fauteuil), 409 (créneau pris). */
    public RendezVousResponse reserver(ReservationRequest request) {
        RendezVous rdv = reservationEngine.reserver(new DemandeReservation(
                request.patientId(), request.praticienId(), request.fauteuilId(), request.acteCode(),
                request.debut(), false, request.etapePlanId(), request.motif()));
        log.info("Rendez-vous {} réservé : patient {} avec {} le {}", rdv.getId(), rdv.getPatientId(),
                rdv.getPraticienId(), rdv.getDebut());
        return RendezVousResponse.from(rdv);
    }

    @Transactional(readOnly = true)
    public List<RendezVousResponse> lister(String patientId, String praticienId, LocalDate date) {
        List<RendezVous> rdvs = date != null
                ? rendezVousRepository.findByDebutGreaterThanEqualAndDebutLessThanOrderByDebutAsc(
                        date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                : rendezVousRepository.findAllByOrderByDebutAsc();
        return rdvs.stream()
                .filter(r -> patientId == null || patientId.isBlank() || r.getPatientId().equals(patientId))
                .filter(r -> praticienId == null || praticienId.isBlank() || r.getPraticienId().equals(praticienId))
                .map(RendezVousResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public RendezVousResponse get(Long id) {
        return RendezVousResponse.from(getEntity(id));
    }

    /** PATCH /api/rdv/{id}/annuler — libère le créneau et l'offre 15 minutes à la liste d'attente. */
    public RendezVousResponse annuler(Long id) {
        RendezVous rdv = getConfirme(id);
        rdv.setStatut(StatutRdv.ANNULE);
        rdv.setMisAJourLe(LocalDateTime.now());
        rendezVousRepository.saveAndFlush(rdv);
        log.info("Rendez-vous {} annulé : créneau du {} libéré", id, rdv.getDebut());
        listeAttenteService.proposerCreneauLibere(rdv);
        return RendezVousResponse.from(rdv);
    }

    /**
     * PATCH /api/rdv/{id}/deplacer — l'ancien passe à DEPLACE, un nouveau rendez-vous CONFIRME est créé.
     * Tout ou rien : si le nouveau créneau est pris (409), l'ancien reste CONFIRME.
     */
    public RendezVousResponse deplacer(Long id, DeplacementRequest request) {
        RendezVous ancien = getConfirme(id);
        ancien.setStatut(StatutRdv.DEPLACE);
        ancien.setMisAJourLe(LocalDateTime.now());
        rendezVousRepository.saveAndFlush(ancien); // l'ancien créneau ne bloque plus le nouveau

        Long fauteuilId = request.fauteuilId() != null ? request.fauteuilId() : ancien.getFauteuil().getId();
        RendezVous nouveau = reservationEngine.reserver(new DemandeReservation(
                ancien.getPatientId(), ancien.getPraticienId(), fauteuilId, ancien.getActeCode(),
                request.nouveauDebut(), ancien.isUrgence(), ancien.getEtapePlanId(), ancien.getMotif()));
        log.info("Rendez-vous {} déplacé du {} au {} (nouveau rendez-vous {})", id, ancien.getDebut(),
                nouveau.getDebut(), nouveau.getId());
        listeAttenteService.proposerCreneauLibere(ancien);
        return RendezVousResponse.from(nouveau);
    }

    public RendezVousResponse terminer(Long id) {
        return changerStatut(id, StatutRdv.TERMINE);
    }

    public RendezVousResponse marquerAbsent(Long id) {
        return changerStatut(id, StatutRdv.ABSENT);
    }

    /**
     * POST /api/rdv/urgence — premier créneau libre dans les 3 jours (complication signalée).
     * S7 : déclenché automatiquement par l'événement ComplicationSuspectee du Suivi post-opératoire.
     */
    public RendezVousResponse reserverUrgence(UrgenceRequest request) {
        String acte = (request.acteCode() == null || request.acteCode().isBlank()) ? ACTE_URGENCE : request.acteCode();
        List<CreneauResponse> creneaux = creneauService.rechercher(acte, request.praticienId(), LocalDate.now(), JOURS_URGENCE);
        for (CreneauResponse c : creneaux.stream().limit(10).toList()) {
            try {
                RendezVous rdv = reservationEngine.reserver(new DemandeReservation(
                        request.patientId(), c.praticienId(), c.fauteuilId(), acte, c.debut(), true, null,
                        request.motif() != null ? request.motif() : "Urgence"));
                log.warn("Créneau d'urgence {} réservé pour le patient {} le {}", rdv.getId(), rdv.getPatientId(), rdv.getDebut());
                return RendezVousResponse.from(rdv);
            } catch (ApiException e) {
                if (e.getStatus() != HttpStatus.CONFLICT) {
                    throw e;
                }
                // pris entre-temps par une autre réservation : on essaie le suivant
            }
        }
        throw new ApiException(HttpStatus.CONFLICT, "Aucun créneau d'urgence libre dans les " + JOURS_URGENCE + " prochains jours");
    }

    private RendezVousResponse changerStatut(Long id, StatutRdv statut) {
        RendezVous rdv = getConfirme(id);
        rdv.setStatut(statut);
        rdv.setMisAJourLe(LocalDateTime.now());
        return RendezVousResponse.from(rdv);
    }

    private RendezVous getConfirme(Long id) {
        RendezVous rdv = getEntity(id);
        if (rdv.getStatut() != StatutRdv.CONFIRME) {
            throw new ApiException(HttpStatus.CONFLICT, "Rendez-vous " + id + " déjà " + rdv.getStatut());
        }
        return rdv;
    }

    private RendezVous getEntity(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Rendez-vous introuvable : " + id));
    }
}
