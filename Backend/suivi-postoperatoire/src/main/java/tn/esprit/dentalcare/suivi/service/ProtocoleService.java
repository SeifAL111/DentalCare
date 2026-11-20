package tn.esprit.dentalcare.suivi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.suivi.dto.ProtocoleRequest;
import tn.esprit.dentalcare.suivi.dto.ProtocoleResponse;
import tn.esprit.dentalcare.suivi.dto.QuestionResponse;
import tn.esprit.dentalcare.suivi.entity.Protocole;
import tn.esprit.dentalcare.suivi.entity.QuestionSuivi;
import tn.esprit.dentalcare.suivi.exception.ConflictException;
import tn.esprit.dentalcare.suivi.exception.ResourceNotFoundException;
import tn.esprit.dentalcare.suivi.repository.ProtocoleRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant la création et la consultation des protocoles post-opératoires.
 */
@Service
@Transactional
public class ProtocoleService {

    private final ProtocoleRepository protocoleRepository;

    public ProtocoleService(ProtocoleRepository protocoleRepository) {
        this.protocoleRepository = protocoleRepository;
    }

    public ProtocoleResponse createProtocole(ProtocoleRequest request) {
        if (protocoleRepository.existsByActeCode(request.getActeCode())) {
            throw new ConflictException("Un protocole existe déjà pour le code d'acte : " + request.getActeCode());
        }

        Protocole protocole = new Protocole(
                request.getActeCode(),
                request.getJours(),
                request.getConsignes()
        );

        if (request.getQuestions() != null) {
            request.getQuestions().forEach(qDto -> {
                QuestionSuivi question = new QuestionSuivi(qDto.getLibelle(), qDto.getSeuilAlerte());
                protocole.addQuestion(question);
            });
        }

        Protocole saved = protocoleRepository.save(protocole);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProtocoleResponse> getAllProtocoles() {
        return protocoleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProtocoleResponse getProtocoleById(Long id) {
        Protocole protocole = protocoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocole introuvable avec l'identifiant : " + id));
        return mapToResponse(protocole);
    }

    public void deleteProtocole(Long id) {
        Protocole protocole = protocoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocole introuvable avec l'identifiant : " + id));
        protocoleRepository.delete(protocole);
    }

    public ProtocoleResponse mapToResponse(Protocole protocole) {
        List<QuestionResponse> questionResponses = protocole.getQuestions().stream()
                .map(q -> new QuestionResponse(q.getId(), q.getLibelle(), q.getSeuilAlerte()))
                .collect(Collectors.toList());

        return new ProtocoleResponse(
                protocole.getId(),
                protocole.getActeCode(),
                protocole.getJours(),
                protocole.getConsignes(),
                questionResponses
        );
    }
}
