package tn.esprit.dentalcare.agendaclinique.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;
import tn.esprit.dentalcare.agendaclinique.dto.FauteuilDtos.FauteuilRequest;
import tn.esprit.dentalcare.agendaclinique.dto.FauteuilDtos.FauteuilResponse;
import tn.esprit.dentalcare.agendaclinique.repository.FauteuilRepository;
import tn.esprit.dentalcare.agendaclinique.web.ApiException;

@Service
@Transactional
public class FauteuilService {

    private final FauteuilRepository fauteuilRepository;

    public FauteuilService(FauteuilRepository fauteuilRepository) {
        this.fauteuilRepository = fauteuilRepository;
    }

    @Transactional(readOnly = true)
    public List<FauteuilResponse> lister() {
        return fauteuilRepository.findAllByOrderByNumeroAsc().stream().map(FauteuilResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FauteuilResponse get(Long id) {
        return FauteuilResponse.from(getEntity(id));
    }

    public FauteuilResponse creer(FauteuilRequest request) {
        String numero = request.numero().trim();
        if (fauteuilRepository.existsByNumero(numero)) {
            throw new ApiException(HttpStatus.CONFLICT, "Un fauteuil porte déjà le numéro " + numero);
        }
        Fauteuil f = new Fauteuil();
        f.setNumero(numero);
        f.setSalle(request.salle().trim());
        f.setActif(true);
        return FauteuilResponse.from(fauteuilRepository.save(f));
    }

    /** Hors service : plus aucune nouvelle réservation sur ce fauteuil (les rendez-vous existants restent). */
    public FauteuilResponse changerEtat(Long id, boolean actif) {
        Fauteuil f = getEntity(id);
        f.setActif(actif);
        return FauteuilResponse.from(f);
    }

    private Fauteuil getEntity(Long id) {
        return fauteuilRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Fauteuil introuvable : " + id));
    }
}
