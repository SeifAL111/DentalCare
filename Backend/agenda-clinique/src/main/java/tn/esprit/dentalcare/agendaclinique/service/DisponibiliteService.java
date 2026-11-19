package tn.esprit.dentalcare.agendaclinique.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;
import tn.esprit.dentalcare.agendaclinique.dto.DisponibiliteDtos.DisponibiliteRequest;
import tn.esprit.dentalcare.agendaclinique.dto.DisponibiliteDtos.DisponibiliteResponse;
import tn.esprit.dentalcare.agendaclinique.repository.DisponibiliteRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;

@Service
@Transactional
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;

    public DisponibiliteService(DisponibiliteRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibiliteResponse> lister(String praticienId) {
        List<Disponibilite> plages = (praticienId == null || praticienId.isBlank())
                ? disponibiliteRepository.findAllByOrderByPraticienIdAscJourAscDebutAsc()
                : disponibiliteRepository.findByPraticienIdOrderByJourAscDebutAsc(praticienId);
        return plages.stream().map(DisponibiliteResponse::from).toList();
    }

    public DisponibiliteResponse creer(DisponibiliteRequest request) {
        if (!request.debut().isBefore(request.fin())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "L'heure de début doit précéder l'heure de fin");
        }
        boolean chevauche = disponibiliteRepository.findByPraticienIdAndJour(request.praticienId(), request.jour())
                .stream()
                .anyMatch(p -> p.chevauche(request.debut(), request.fin()));
        if (chevauche) {
            throw new ApiException(HttpStatus.CONFLICT, "Cette plage chevauche une plage existante du praticien");
        }
        Disponibilite d = new Disponibilite();
        d.setPraticienId(request.praticienId().trim());
        d.setJour(request.jour());
        d.setDebut(request.debut());
        d.setFin(request.fin());
        return DisponibiliteResponse.from(disponibiliteRepository.save(d));
    }

    public void supprimer(Long id) {
        Disponibilite d = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Disponibilité introuvable : " + id));
        disponibiliteRepository.delete(d);
    }
}
