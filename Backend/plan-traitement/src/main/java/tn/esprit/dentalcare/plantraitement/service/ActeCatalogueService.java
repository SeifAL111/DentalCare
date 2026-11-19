package tn.esprit.dentalcare.plantraitement.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.plantraitement.domain.ActeCatalogue;
import tn.esprit.dentalcare.plantraitement.dto.ActeCatalogueRequest;
import tn.esprit.dentalcare.plantraitement.dto.ActeCatalogueResponse;
import tn.esprit.dentalcare.plantraitement.repository.ActeCatalogueRepository;
import tn.esprit.dentalcare.plantraitement.web.ApiException;

@Service
@Transactional
public class ActeCatalogueService {

    private final ActeCatalogueRepository repository;

    public ActeCatalogueService(ActeCatalogueRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ActeCatalogueResponse> findAll() {
        return repository.findAll().stream().map(ActeCatalogueResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ActeCatalogueResponse findByCode(String code) {
        return ActeCatalogueResponse.from(getEntity(code));
    }

    public ActeCatalogueResponse create(ActeCatalogueRequest request) {
        if (repository.existsById(request.code())) {
            throw new ApiException(HttpStatus.CONFLICT, "Acte already exists: " + request.code());
        }
        ActeCatalogue acte = map(request);
        return ActeCatalogueResponse.from(repository.save(acte));
    }

    public ActeCatalogueResponse update(String code, ActeCatalogueRequest request) {
        ActeCatalogue acte = getEntity(code);
        acte.setLibelle(request.libelle());
        acte.setDureeMinutes(request.dureeMinutes());
        acte.setTarif(request.tarif());
        acte.setDelaiCicatrisationJours(request.delaiCicatrisationJours());
        acte.setCompetenceRequise(request.competenceRequise());
        return ActeCatalogueResponse.from(repository.save(acte));
    }

    public void delete(String code) {
        if (!repository.existsById(code)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Acte not found: " + code);
        }
        repository.deleteById(code);
    }

    public ActeCatalogue getEntity(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Acte not found: " + code));
    }

    private ActeCatalogue map(ActeCatalogueRequest request) {
        ActeCatalogue acte = new ActeCatalogue();
        acte.setCode(request.code());
        acte.setLibelle(request.libelle());
        acte.setDureeMinutes(request.dureeMinutes());
        acte.setTarif(request.tarif());
        acte.setDelaiCicatrisationJours(request.delaiCicatrisationJours());
        acte.setCompetenceRequise(request.competenceRequise());
        return acte;
    }
}
