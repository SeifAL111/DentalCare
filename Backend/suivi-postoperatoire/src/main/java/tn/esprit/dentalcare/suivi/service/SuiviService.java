package tn.esprit.dentalcare.suivi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.suivi.dto.ConsignesResponse;
import tn.esprit.dentalcare.suivi.dto.SuiviRequest;
import tn.esprit.dentalcare.suivi.dto.SuiviResponse;
import tn.esprit.dentalcare.suivi.entity.Protocole;
import tn.esprit.dentalcare.suivi.entity.StatutSuivi;
import tn.esprit.dentalcare.suivi.entity.Suivi;
import tn.esprit.dentalcare.suivi.exception.ConflictException;
import tn.esprit.dentalcare.suivi.exception.ResourceNotFoundException;
import tn.esprit.dentalcare.suivi.repository.ProtocoleRepository;
import tn.esprit.dentalcare.suivi.repository.SuiviRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant le cycle de vie du suivi post-opératoire :
 * ouverture, consultation, consignes, prise en charge et clôture.
 */
@Service
@Transactional
public class SuiviService {

    private final SuiviRepository suiviRepository;
    private final ProtocoleRepository protocoleRepository;

    public SuiviService(SuiviRepository suiviRepository, ProtocoleRepository protocoleRepository) {
        this.suiviRepository = suiviRepository;
        this.protocoleRepository = protocoleRepository;
    }

    public SuiviResponse openSuivi(SuiviRequest request) {
        Protocole protocole = protocoleRepository.findById(request.getProtocoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Protocole introuvable avec l'identifiant : " + request.getProtocoleId()));

        Suivi suivi = new Suivi(
                request.getPatientId(),
                request.getActeId(),
                protocole
        );

        Suivi saved = suiviRepository.save(suivi);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SuiviResponse> getSuivisByPatient(String patientId) {
        List<Suivi> suivis = suiviRepository.findByPatientId(patientId);
        if (suivis.isEmpty()) {
            throw new ResourceNotFoundException("Aucun suivi trouvé pour le patient : " + patientId);
        }
        return suivis.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsignesResponse getConsignes(Long suiviId) {
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));

        return new ConsignesResponse(
                suivi.getId(),
                suivi.getProtocole().getActeCode(),
                suivi.getProtocole().getConsignes()
        );
    }

    @Transactional(readOnly = true)
    public List<SuiviResponse> getSuivisEnAlerte() {
        return suiviRepository.findByStatut(StatutSuivi.EN_ALERTE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SuiviResponse prendreEnCharge(Long suiviId) {
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));

        if (suivi.getStatut() != StatutSuivi.EN_ALERTE) {
            throw new ConflictException("Impossible de prendre en charge ce suivi. Seul un suivi au statut EN_ALERTE peut être pris en charge (statut actuel : " + suivi.getStatut() + ")");
        }

        suivi.setStatut(StatutSuivi.PRIS_EN_CHARGE);
        Suivi updated = suiviRepository.save(suivi);
        return mapToResponse(updated);
    }

    public SuiviResponse cloturer(Long suiviId) {
        // TODO Keycloak rôle DENTISTE (S8) : Seul un utilisateur avec le rôle dentiste pourra clôturer
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));

        if (suivi.getStatut() == StatutSuivi.CLOTURE) {
            throw new ConflictException("Le suivi avec l'identifiant " + suiviId + " est déjà clôturé.");
        }

        suivi.setStatut(StatutSuivi.CLOTURE);
        Suivi updated = suiviRepository.save(suivi);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public SuiviResponse getSuiviById(Long suiviId) {
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));
        return mapToResponse(suivi);
    }

    public void deleteSuivi(Long suiviId) {
        Suivi suivi = suiviRepository.findById(suiviId)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi introuvable avec l'identifiant : " + suiviId));
        suiviRepository.delete(suivi);
    }

    public SuiviResponse mapToResponse(Suivi suivi) {
        return new SuiviResponse(
                suivi.getId(),
                suivi.getPatientId(),
                suivi.getActeId(),
                suivi.isRenforce(),
                suivi.getStatut(),
                suivi.getProtocole().getId(),
                suivi.getProtocole().getActeCode(),
                suivi.getDateOuverture()
        );
    }
}
