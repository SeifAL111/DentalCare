package tn.esprit.dentalcare.suivi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.dentalcare.suivi.dto.ReponseRequest;
import tn.esprit.dentalcare.suivi.dto.ReponseResponse;
import tn.esprit.dentalcare.suivi.entity.*;
import tn.esprit.dentalcare.suivi.exception.BadRequestException;
import tn.esprit.dentalcare.suivi.exception.ConflictException;
import tn.esprit.dentalcare.suivi.exception.ResourceNotFoundException;
import tn.esprit.dentalcare.suivi.repository.AlerteRepository;
import tn.esprit.dentalcare.suivi.repository.QuestionSuiviRepository;
import tn.esprit.dentalcare.suivi.repository.ReponsePatientRepository;
import tn.esprit.dentalcare.suivi.repository.SuiviRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReponseServiceTest {

    @Mock
    private ReponsePatientRepository reponsePatientRepository;

    @Mock
    private SuiviRepository suiviRepository;

    @Mock
    private QuestionSuiviRepository questionSuiviRepository;

    @Mock
    private AlerteRepository alerteRepository;

    @InjectMocks
    private ReponseService reponseService;

    private Suivi suiviActif;
    private Protocole protocole;
    private QuestionSuivi questionDouleur;

    @BeforeEach
    void setUp() {
        protocole = new Protocole("DEVITALISATION", Arrays.asList(1, 3), "Consignes de test");
        protocole.setId(1L);

        questionDouleur = new QuestionSuivi("Quelle est votre douleur (0-10) ?", 7);
        questionDouleur.setId(10L);
        questionDouleur.setProtocole(protocole);

        suiviActif = new Suivi("PATIENT-AHMED", 101L, protocole);
        suiviActif.setId(100L);
        suiviActif.setStatut(StatutSuivi.ACTIF);
    }

    @Test
    @DisplayName("1. Réponse sous le seuil -> pas d'alerte, statut reste ACTIF")
    void testReponse_SousLeSeuil_PasDAlerte() {
        // Given : valeur 3 < seuil 7
        ReponseRequest request = new ReponseRequest(1, 10L, 3);

        when(suiviRepository.findById(100L)).thenReturn(Optional.of(suiviActif));
        when(questionSuiviRepository.findById(10L)).thenReturn(Optional.of(questionDouleur));
        when(reponsePatientRepository.existsBySuiviIdAndJourAndQuestionId(100L, 1, 10L)).thenReturn(false);
        when(reponsePatientRepository.save(any(ReponsePatient.class))).thenAnswer(invocation -> {
            ReponsePatient r = invocation.getArgument(0);
            r.setId(500L);
            return r;
        });

        // When
        ReponseResponse response = reponseService.enregistrerReponse(100L, request);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getValeur());
        assertFalse(response.isAlerteCreee(), "Aucune alerte ne doit être créée car valeur < seuil");
        assertNull(response.getMotifAlerte());
        assertEquals(StatutSuivi.ACTIF, response.getNouveauStatutSuivi());

        verify(alerteRepository, never()).save(any(Alerte.class));
        verify(suiviRepository, never()).save(any(Suivi.class));
    }

    @Test
    @DisplayName("2. Réponse >= seuil -> Alerte créée et suivi passe à EN_ALERTE")
    void testReponse_AuDessusDuSeuil_AlerteDeclenchee() {
        // Given : valeur 8 >= seuil 7
        ReponseRequest request = new ReponseRequest(1, 10L, 8);

        when(suiviRepository.findById(100L)).thenReturn(Optional.of(suiviActif));
        when(questionSuiviRepository.findById(10L)).thenReturn(Optional.of(questionDouleur));
        when(reponsePatientRepository.existsBySuiviIdAndJourAndQuestionId(100L, 1, 10L)).thenReturn(false);
        when(reponsePatientRepository.save(any(ReponsePatient.class))).thenAnswer(invocation -> {
            ReponsePatient r = invocation.getArgument(0);
            r.setId(501L);
            return r;
        });

        // When
        ReponseResponse response = reponseService.enregistrerReponse(100L, request);

        // Then
        assertNotNull(response);
        assertEquals(8, response.getValeur());
        assertTrue(response.isAlerteCreee(), "Une alerte doit être levée car valeur >= seuil");
        assertEquals("Douleur 8 à J+1 (seuil 7)", response.getMotifAlerte());
        assertEquals(StatutSuivi.EN_ALERTE, response.getNouveauStatutSuivi());

        verify(alerteRepository, times(1)).save(any(Alerte.class));
        verify(suiviRepository, times(1)).save(suiviActif);
    }

    @Test
    @DisplayName("3. Doublon de réponse (même suivi + même jour + même question) -> ConflictException (409)")
    void testReponse_Doublon_DoitLancerConflictException() {
        ReponseRequest request = new ReponseRequest(1, 10L, 4);

        when(suiviRepository.findById(100L)).thenReturn(Optional.of(suiviActif));
        when(questionSuiviRepository.findById(10L)).thenReturn(Optional.of(questionDouleur));
        when(reponsePatientRepository.existsBySuiviIdAndJourAndQuestionId(100L, 1, 10L)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () ->
                reponseService.enregistrerReponse(100L, request));

        assertTrue(ex.getMessage().contains("Une réponse a déjà été enregistrée"));
        verify(reponsePatientRepository, never()).save(any(ReponsePatient.class));
    }

    @Test
    @DisplayName("4. Réponse sur suivi CLOTURE -> ConflictException (409)")
    void testReponse_SuiviCloture_DoitLancerConflictException() {
        suiviActif.setStatut(StatutSuivi.CLOTURE);
        ReponseRequest request = new ReponseRequest(1, 10L, 2);

        when(suiviRepository.findById(100L)).thenReturn(Optional.of(suiviActif));

        ConflictException ex = assertThrows(ConflictException.class, () ->
                reponseService.enregistrerReponse(100L, request));

        assertTrue(ex.getMessage().contains("le suivi est clôturé"));
        verify(reponsePatientRepository, never()).save(any(ReponsePatient.class));
    }

    @Test
    @DisplayName("5. Jour hors liste du protocole -> BadRequestException (400)")
    void testReponse_JourHorsProtocole_DoitLancerBadRequestException() {
        // Jour 5 n'est pas dans [1, 3]
        ReponseRequest request = new ReponseRequest(5, 10L, 2);

        when(suiviRepository.findById(100L)).thenReturn(Optional.of(suiviActif));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                reponseService.enregistrerReponse(100L, request));

        assertTrue(ex.getMessage().contains("n'appartient pas à la liste des jours prévus"));
    }
}
