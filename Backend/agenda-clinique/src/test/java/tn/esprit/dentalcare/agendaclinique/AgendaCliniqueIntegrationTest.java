package tn.esprit.dentalcare.agendaclinique;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import tn.esprit.dentalcare.agendaclinique.domain.OffreCreneau;
import tn.esprit.dentalcare.agendaclinique.domain.StatutOffre;
import tn.esprit.dentalcare.agendaclinique.domain.StatutRdv;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.InscriptionRequest;
import tn.esprit.dentalcare.agendaclinique.dto.ListeAttenteDtos.OffreResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.CreneauResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.RendezVousResponse;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.ReservationRequest;
import tn.esprit.dentalcare.agendaclinique.dto.RendezVousDtos.UrgenceRequest;
import tn.esprit.dentalcare.agendaclinique.repository.FauteuilRepository;
import tn.esprit.dentalcare.agendaclinique.repository.OffreCreneauRepository;
import tn.esprit.dentalcare.agendaclinique.service.CreneauService;
import tn.esprit.dentalcare.agendaclinique.service.ListeAttenteService;
import tn.esprit.dentalcare.agendaclinique.service.RendezVousService;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;
import tn.esprit.dentalcare.agendaclinique.web.OffreExpireeException;

/**
 * Règles métier de l'Agenda Clinique, sur la vraie base H2 et les données de démo
 * (PRAT-001 : lundi 08:30-12:30 et 14:00-18:00 ; PRAT-002 : lundi 09:00-13:00 ; fauteuils F1, F2, F3).
 * Chaque test travaille sur une semaine ou un praticien différent pour ne pas gêner les autres.
 */
@SpringBootTest(properties = {"eureka.client.enabled=false", "agenda.offres.verification-ms=3600000"})
class AgendaCliniqueIntegrationTest {

    @Autowired
    private RendezVousService rendezVousService;

    @Autowired
    private ListeAttenteService listeAttenteService;

    @Autowired
    private CreneauService creneauService;

    @Autowired
    private FauteuilRepository fauteuilRepository;

    @Autowired
    private OffreCreneauRepository offreRepository;

    /** Lundi prochain + n semaines, à l'heure donnée. */
    private static LocalDateTime lundi(int semaines, int heure, int minute) {
        return LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).plusWeeks(semaines).atTime(heure, minute);
    }

    private Long fauteuil(String numero) {
        return fauteuilRepository.findByNumero(numero).orElseThrow().getId();
    }

    private ReservationRequest demande(String patient, String praticien, String fauteuil, String acte, LocalDateTime debut) {
        return new ReservationRequest(patient, praticien, fauteuil(fauteuil), acte, debut, null, null);
    }

    @Test
    void reservationPuisFauteuilDejaPrisRenvoie409() {
        LocalDateTime debut = lundi(1, 9, 0);
        RendezVousResponse rdv = rendezVousService.reserver(demande("PAT-A", "PRAT-001", "F1", "DEVIT", debut));
        assertEquals(StatutRdv.CONFIRME, rdv.statut());
        assertEquals(debut.plusMinutes(60), rdv.fin());

        // Autre praticien, même fauteuil, créneau qui chevauche (09:30)
        ApiException ex = assertThrows(ApiException.class, () -> rendezVousService.reserver(
                demande("PAT-B", "PRAT-002", "F1", "DETARTRAGE", debut.plusMinutes(30))));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void praticienDejaOccupeSurUnAutreFauteuilRenvoie409() {
        LocalDateTime debut = lundi(1, 14, 0);
        rendezVousService.reserver(demande("PAT-A2", "PRAT-001", "F2", "DEVIT", debut));

        ApiException ex = assertThrows(ApiException.class, () -> rendezVousService.reserver(
                demande("PAT-B2", "PRAT-001", "F3", "DETARTRAGE", debut.plusMinutes(15))));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void horsDisponibiliteDuPraticienRenvoie400() {
        // PRAT-001 ne travaille pas entre 12:30 et 14:00
        ApiException ex = assertThrows(ApiException.class, () -> rendezVousService.reserver(
                demande("PAT-X", "PRAT-001", "F1", "DETARTRAGE", lundi(1, 13, 0))));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void deuxReservationsSimultaneesDuMemeCreneauUneSeuleReussit() throws Exception {
        LocalDateTime debut = lundi(2, 10, 0);
        AtomicInteger succes = new AtomicInteger();
        AtomicInteger echecs = new AtomicInteger();
        CountDownLatch depart = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        for (String patient : List.of("PAT-C1", "PAT-C2")) {
            pool.submit(() -> {
                try {
                    depart.await();
                    rendezVousService.reserver(demande(patient, "PRAT-002", "F3", "DETARTRAGE", debut));
                    succes.incrementAndGet();
                } catch (Exception e) {
                    echecs.incrementAndGet();
                }
                return null;
            });
        }
        depart.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));

        assertEquals(1, succes.get(), "verrouillage transactionnel : une seule réservation doit passer");
        assertEquals(1, echecs.get());
    }

    @Test
    void annulationOffreLeCreneauALaListeDAttenteQuiLAccepte() {
        RendezVousResponse rdv = rendezVousService.reserver(demande("PAT-D", "PRAT-001", "F2", "DETARTRAGE", lundi(3, 10, 0)));
        listeAttenteService.inscrire(new InscriptionRequest("PAT-E", "DETARTRAGE", "PRAT-001"));

        rendezVousService.annuler(rdv.id());

        List<OffreResponse> offres = listeAttenteService.listerOffres("PAT-E", StatutOffre.EN_ATTENTE);
        assertEquals(1, offres.size());
        RendezVousResponse nouveau = listeAttenteService.accepter(offres.get(0).id());
        assertEquals("PAT-E", nouveau.patientId());
        assertEquals(rdv.debut(), nouveau.debut());
        assertEquals(StatutRdv.CONFIRME, nouveau.statut());
    }

    @Test
    void offreExpireeRenvoie410EtPasseAuPatientSuivant() {
        RendezVousResponse rdv = rendezVousService.reserver(demande("PAT-F", "PRAT-002", "F1", "DETARTRAGE", lundi(3, 11, 0)));
        listeAttenteService.inscrire(new InscriptionRequest("PAT-G", "DETARTRAGE", "PRAT-002"));
        listeAttenteService.inscrire(new InscriptionRequest("PAT-H", "DETARTRAGE", "PRAT-002"));
        rendezVousService.annuler(rdv.id());

        OffreResponse offreG = listeAttenteService.listerOffres("PAT-G", StatutOffre.EN_ATTENTE).get(0);
        // On simule les 15 minutes écoulées
        OffreCreneau entite = offreRepository.findById(offreG.id()).orElseThrow();
        entite.setExpireLe(LocalDateTime.now().minusMinutes(1));
        offreRepository.save(entite);

        OffreExpireeException ex = assertThrows(OffreExpireeException.class, () -> listeAttenteService.accepter(offreG.id()));
        assertEquals(HttpStatus.GONE, ex.getStatus());
        assertEquals(StatutOffre.EXPIREE, offreRepository.findById(offreG.id()).orElseThrow().getStatut());
        assertEquals(1, listeAttenteService.listerOffres("PAT-H", StatutOffre.EN_ATTENTE).size());
    }

    @Test
    void rechercheDeCreneauxRespecteLesHorairesEtLaDureeDeLActe() {
        LocalDate jour = lundi(4, 0, 0).toLocalDate();
        List<CreneauResponse> creneaux = creneauService.rechercher("DEVIT", "PRAT-001", jour, 1);

        // 08:30 -> 11:30 et 14:00 -> 17:00, tous les quarts d'heure, pour un acte de 60 min
        assertEquals(26, creneaux.size());
        assertEquals(jour.atTime(8, 30), creneaux.get(0).debut());
        assertTrue(creneaux.stream().allMatch(c -> c.dureeMinutes() == 60));
        assertFalse(creneaux.stream().anyMatch(c -> c.fin().toLocalTime().isAfter(LocalTime.of(18, 0))));
    }

    @Test
    void urgenceReserveLePremierCreneauLibre() {
        RendezVousResponse rdv = rendezVousService.reserverUrgence(
                new UrgenceRequest("PAT-U", null, null, "Douleur 8/10 à J+3"));
        assertTrue(rdv.urgence());
        assertEquals("URGENCE", rdv.acteCode());
        assertTrue(rdv.debut().isBefore(LocalDate.now().plusDays(3).atStartOfDay()));
    }
}
