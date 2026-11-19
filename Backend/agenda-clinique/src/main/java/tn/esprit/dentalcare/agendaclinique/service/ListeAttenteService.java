package tn.esprit.dentalcare.agendaclinique.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.agendaclinique.catalogue.CatalogueActes;
import tn.esprit.dentalcare.agendaclinique.domain.InscriptionAttente;
import tn.esprit.dentalcare.agendaclinique.domain.OffreCreneau;
import tn.esprit.dentalcare.agendaclinique.domain.RendezVous;
import tn.esprit.dentalcare.agendaclinique.domain.StatutInscription;
import tn.esprit.dentalcare.agendaclinique.domain.StatutOffre;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.InscriptionRequest;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.InscriptionResponse;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.OffreResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.RendezVousResponse;
import tn.esprit.dentalcare.agendaclinique.repository.InscriptionAttenteRepository;
import tn.esprit.dentalcare.agendaclinique.repository.OffreCreneauRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;
import tn.esprit.dentalcare.agendaclinique.web.OffreExpireeException;

/**
 * Liste d'attente : un créneau libéré est offert pendant 15 minutes au premier patient compatible,
 * puis au suivant s'il refuse ou ne répond pas.
 * Compatible = même praticien (ou pas de préférence) et un acte qui tient dans la durée du créneau.
 */
@Service
@Transactional
public class ListeAttenteService {

    private static final Logger log = LoggerFactory.getLogger(ListeAttenteService.class);

    private final InscriptionAttenteRepository inscriptionRepository;
    private final OffreCreneauRepository offreRepository;
    private final ReservationEngine reservationEngine;
    private final CatalogueActes catalogueActes;
    private final long dureeOffreMinutes;

    public ListeAttenteService(
            InscriptionAttenteRepository inscriptionRepository,
            OffreCreneauRepository offreRepository,
            ReservationEngine reservationEngine,
            CatalogueActes catalogueActes,
            @Value("${agenda.offres.duree-minutes:15}") long dureeOffreMinutes) {
        this.inscriptionRepository = inscriptionRepository;
        this.offreRepository = offreRepository;
        this.reservationEngine = reservationEngine;
        this.catalogueActes = catalogueActes;
        this.dureeOffreMinutes = dureeOffreMinutes;
    }

    // ---------------------------------------------------------------- inscriptions

    /** POST /api/liste-attente — 201, 400 (acte inconnu), 409 (déjà inscrit pour cet acte). */
    public InscriptionResponse inscrire(InscriptionRequest request) {
        reservationEngine.dureeActe(request.acteCode()); // 400 si acte inconnu
        String acteCode = request.acteCode().trim().toUpperCase(Locale.ROOT);
        if (inscriptionRepository.existsByPatientIdAndActeCodeAndStatut(request.patientId(), acteCode, StatutInscription.EN_ATTENTE)) {
            throw new ApiException(HttpStatus.CONFLICT, "Le patient " + request.patientId()
                    + " est déjà en liste d'attente pour " + acteCode);
        }
        InscriptionAttente i = new InscriptionAttente();
        i.setPatientId(request.patientId().trim());
        i.setActeCode(acteCode);
        i.setPraticienId(request.praticienId() == null || request.praticienId().isBlank() ? null : request.praticienId().trim());
        i.setInscritLe(LocalDateTime.now());
        i.setStatut(StatutInscription.EN_ATTENTE);
        return InscriptionResponse.from(inscriptionRepository.save(i));
    }

    @Transactional(readOnly = true)
    public List<InscriptionResponse> listerEnAttente() {
        return inscriptionRepository.findByStatutOrderByInscritLeAsc(StatutInscription.EN_ATTENTE).stream()
                .map(InscriptionResponse::from)
                .toList();
    }

    /** DELETE /api/liste-attente/{id} — le patient se retire ; son offre en cours passe au suivant. */
    public InscriptionResponse desinscrire(Long id) {
        InscriptionAttente i = inscriptionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inscription introuvable : " + id));
        if (i.getStatut() != StatutInscription.EN_ATTENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Inscription " + id + " déjà " + i.getStatut());
        }
        i.setStatut(StatutInscription.ANNULEE);
        for (OffreCreneau o : offreRepository.findByInscription_IdAndStatut(id, StatutOffre.EN_ATTENTE)) {
            o.setStatut(StatutOffre.ANNULEE);
            proposerCreneauLibere(o.getRendezVousLibere());
        }
        return InscriptionResponse.from(i);
    }

    // ---------------------------------------------------------------- offres

    /**
     * Offre le créneau d'un rendez-vous annulé/déplacé au premier patient compatible de la liste.
     * Appelée dans la transaction de l'annulation, du déplacement, d'un refus ou d'une expiration.
     */
    public Optional<OffreCreneau> proposerCreneauLibere(RendezVous libere) {
        LocalDateTime maintenant = LocalDateTime.now();
        if (!libere.getDebut().isAfter(maintenant)) {
            return Optional.empty(); // créneau passé
        }
        List<OffreCreneau> offresDuCreneau = offreRepository.findByRendezVousLibere_Id(libere.getId());
        if (offresDuCreneau.stream().anyMatch(o -> o.getStatut() == StatutOffre.EN_ATTENTE)) {
            return Optional.empty(); // déjà offert à quelqu'un
        }
        if (!reservationEngine.creneauLibre(libere.getPraticienId(), libere.getFauteuil().getId(), libere.getDebut(), libere.getFin())) {
            return Optional.empty(); // repris entre-temps
        }
        Set<Long> dejaSollicitees = offresDuCreneau.stream()
                .map(o -> o.getInscription().getId())
                .collect(Collectors.toSet());

        for (InscriptionAttente i : inscriptionRepository.findByStatutOrderByInscritLeAsc(StatutInscription.EN_ATTENTE)) {
            if (dejaSollicitees.contains(i.getId())
                    || i.getPatientId().equals(libere.getPatientId())
                    || (i.getPraticienId() != null && !i.getPraticienId().equals(libere.getPraticienId()))
                    || offreRepository.existsByInscription_IdAndStatut(i.getId(), StatutOffre.EN_ATTENTE)) {
                continue;
            }
            OptionalInt dureeActe = catalogueActes.dureeMinutes(i.getActeCode());
            if (dureeActe.isEmpty() || dureeActe.getAsInt() > libere.getDureeMinutes()) {
                continue;
            }
            OffreCreneau offre = new OffreCreneau();
            offre.setRendezVousLibere(libere);
            offre.setInscription(i);
            offre.setCreeLe(maintenant);
            offre.setExpireLe(maintenant.plusMinutes(dureeOffreMinutes));
            offre.setStatut(StatutOffre.EN_ATTENTE);
            offreRepository.save(offre);
            // S7 : publication de l'événement CreneauLibere -> Coordination (agent Secrétaire prévient le patient)
            log.info("Créneau du {} offert au patient {} jusqu'à {}", libere.getDebut(), i.getPatientId(), offre.getExpireLe());
            return Optional.of(offre);
        }
        log.info("Créneau du {} libéré : aucun patient compatible en liste d'attente", libere.getDebut());
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<OffreResponse> listerOffres(String patientId, StatutOffre statut) {
        List<OffreCreneau> offres = (patientId != null && !patientId.isBlank())
                ? offreRepository.findByInscription_PatientIdOrderByCreeLeDesc(patientId)
                : (statut != null ? offreRepository.findByStatutOrderByCreeLeDesc(statut) : offreRepository.findAllByOrderByCreeLeDesc());
        return offres.stream()
                .filter(o -> statut == null || o.getStatut() == statut)
                .map(OffreResponse::from)
                .toList();
    }

    /**
     * POST /api/offres/{id}/accepter — 200 (rendez-vous créé), 404, 409 (créneau repris), 410 (offre expirée).
     * noRollbackFor : en cas de 410, l'expiration et l'offre au patient suivant sont quand même enregistrées.
     */
    @Transactional(noRollbackFor = OffreExpireeException.class)
    public RendezVousResponse accepter(Long offreId) {
        OffreCreneau offre = getOffre(offreId);
        if (offre.getStatut() == StatutOffre.EXPIREE) {
            throw new OffreExpireeException(offreId);
        }
        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Offre " + offreId + " déjà " + offre.getStatut());
        }
        if (LocalDateTime.now().isAfter(offre.getExpireLe())) {
            expirer(offre);
            throw new OffreExpireeException(offreId);
        }

        RendezVous libere = offre.getRendezVousLibere();
        InscriptionAttente inscription = offre.getInscription();
        RendezVous nouveau = reservationEngine.reserver(new DemandeReservation(
                inscription.getPatientId(), libere.getPraticienId(), libere.getFauteuil().getId(),
                inscription.getActeCode(), libere.getDebut(), false, null, "Liste d'attente"));

        offre.setStatut(StatutOffre.ACCEPTEE);
        offre.setRendezVousCreeId(nouveau.getId());
        inscription.setStatut(StatutInscription.SERVIE);
        log.info("Offre {} acceptée : rendez-vous {} pour le patient {}", offreId, nouveau.getId(), inscription.getPatientId());
        return RendezVousResponse.from(nouveau);
    }

    /** POST /api/offres/{id}/refuser — le créneau passe immédiatement au patient suivant. */
    public OffreResponse refuser(Long offreId) {
        OffreCreneau offre = getOffre(offreId);
        if (offre.getStatut() != StatutOffre.EN_ATTENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Offre " + offreId + " déjà " + offre.getStatut());
        }
        offre.setStatut(StatutOffre.REFUSEE);
        offreRepository.saveAndFlush(offre);
        proposerCreneauLibere(offre.getRendezVousLibere());
        return OffreResponse.from(offre);
    }

    /** Appelée chaque minute par OffreExpirationJob (et par POST /api/offres/verifier-expirations). */
    public int expirerOffresEchues() {
        List<OffreCreneau> echues = offreRepository.findByStatutAndExpireLeBefore(StatutOffre.EN_ATTENTE, LocalDateTime.now());
        echues.forEach(this::expirer);
        return echues.size();
    }

    private void expirer(OffreCreneau offre) {
        offre.setStatut(StatutOffre.EXPIREE);
        offreRepository.saveAndFlush(offre);
        log.info("Offre {} expirée (patient {})", offre.getId(), offre.getInscription().getPatientId());
        proposerCreneauLibere(offre.getRendezVousLibere());
    }

    private OffreCreneau getOffre(Long id) {
        return offreRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Offre introuvable : " + id));
    }
}
