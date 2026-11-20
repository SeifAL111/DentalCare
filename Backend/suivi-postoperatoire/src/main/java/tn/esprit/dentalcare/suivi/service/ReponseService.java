package tn.esprit.dentalcare.suivi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.suivi.dto.AlerteResponse;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant l'enregistrement des réponses des patients et le déclenchement
 * automatique des alertes cliniques selon les seuils paramétrés.
 */
@Service
@Transactional
public class ReponseService {

    private final ReponsePatientRepository reponsePatientRepository;
    private final SuiviRepository suiviRepository;
    private final QuestionSuiviRepository questionSuiviRepository;
    private final AlerteRepository alerteRepository;

    public ReponseService(ReponsePatientRepository reponsePatientRepository,
                          SuiviRepository suiviRepository,
                          QuestionSuiviRepository questionSuiviRepository,
                          AlerteRepository alerteRepository) {
        this.reponsePatientRepository = reponsePatientRepository;
        this.suiviRepository = suiviRepository;
        this.questionSuiviRepository = questionSuiviRepository;
        this.alerteRepository = alerteRepository;
    }

    public ReponseResponse enregistrerReponse(Long suiviId, ReponseRequest request) {
        // 1. Validation de la plage de la valeur
        if (request.getValeur() == null || request.getValeur() < 0 || request.getValeur() > 10) {
            throw new BadRequestException("La valeur de la réponse doit être comprise entre 0 et 10.");
        }

        // 2. Vérification de l'existence du suivi
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));

        // 3. Règle métier : Interdit si suivi clôturé -> 409 Conflict
        if (suivi.getStatut() == StatutSuivi.CLOTURE) {
            throw new ConflictException("Impossible d'enregistrer une réponse : le suivi est clôturé.");
        }

        Protocole protocole = suivi.getProtocole();

        // 4. Règle métier : Le jour doit être prévu par le protocole -> 400 Bad Request
        if (protocole.getJours() == null || !protocole.getJours().contains(request.getJour())) {
            throw new BadRequestException("Le jour " + request.getJour() + " n'appartient pas à la liste des jours prévus par le protocole : " + protocole.getJours());
        }

        // 5. Vérification de l'existence de la question -> 404 Not Found
        QuestionSuivi question = questionSuiviRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question introuvable avec l'identifiant : " + request.getQuestionId()));

        // 6. Règle métier : La question doit appartenir au protocole du suivi -> 400 Bad Request
        if (question.getProtocole() == null || !question.getProtocole().getId().equals(protocole.getId())) {
            throw new BadRequestException("La question n°" + question.getId() + " n'appartient pas au protocole (" + protocole.getActeCode() + ") associé à ce suivi.");
        }

        // 7. Règle métier : Doublon de réponse interdit (même suivi + même jour + même question) -> 409 Conflict
        if (reponsePatientRepository.existsBySuiviIdAndJourAndQuestionId(suiviId, request.getJour(), request.getQuestionId())) {
            throw new ConflictException("Une réponse a déjà été enregistrée pour ce suivi au jour J+" + request.getJour() + " pour la question n°" + request.getQuestionId());
        }

        // 8. Enregistrement de la réponse
        ReponsePatient reponse = new ReponsePatient(request.getJour(), request.getValeur(), suivi, question);
        ReponsePatient savedReponse = reponsePatientRepository.save(reponse);

        // 9. Évaluation du seuil d'alerte : valeur >= seuilAlerte -> Alerte + statut EN_ALERTE
        boolean alerteCreee = false;
        String motifAlerte = null;

        if (request.getValeur() >= question.getSeuilAlerte()) {
            alerteCreee = true;
            motifAlerte = "Douleur " + request.getValeur() + " à J+" + request.getJour() + " (seuil " + question.getSeuilAlerte() + ")";
            Alerte alerte = new Alerte(motifAlerte, suivi);
            alerteRepository.save(alerte);

            suivi.setStatut(StatutSuivi.EN_ALERTE);
            suiviRepository.save(suivi);
        }

        return new ReponseResponse(
                savedReponse.getId(),
                savedReponse.getJour(),
                savedReponse.getValeur(),
                savedReponse.getRecueLe(),
                suivi.getId(),
                question.getId(),
                question.getLibelle(),
                alerteCreee,
                motifAlerte,
                suivi.getStatut()
        );
    }

    @Transactional(readOnly = true)
    public List<AlerteResponse> getAlertesBySuivi(Long suiviId) {
        return alerteRepository.findBySuiviId(suiviId).stream()
                .map(a -> new AlerteResponse(a.getId(), a.getMotif(), a.getCreeLe(), a.getSuivi().getId(), a.getSuivi().getPatientId()))
                .collect(Collectors.toList());
    }
}
