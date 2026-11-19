package tn.esprit.dentalcare.plantraitement.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.dentalcare.plantraitement.domain.ActeCatalogue;
import tn.esprit.dentalcare.plantraitement.domain.Devis;
import tn.esprit.dentalcare.plantraitement.domain.Echeance;
import tn.esprit.dentalcare.plantraitement.domain.EtapePlan;
import tn.esprit.dentalcare.plantraitement.domain.Facture;
import tn.esprit.dentalcare.plantraitement.domain.PlanTraitement;
import tn.esprit.dentalcare.plantraitement.domain.StatutEtape;
import tn.esprit.dentalcare.plantraitement.domain.StatutPlan;
import tn.esprit.dentalcare.plantraitement.dto.CreatePlanRequest;
import tn.esprit.dentalcare.plantraitement.dto.DevisResponse;
import tn.esprit.dentalcare.plantraitement.dto.EcheanceResponse;
import tn.esprit.dentalcare.plantraitement.dto.EtapeRequest;
import tn.esprit.dentalcare.plantraitement.dto.FactureResponse;
import tn.esprit.dentalcare.plantraitement.dto.PlanResponse;
import tn.esprit.dentalcare.plantraitement.repository.FactureRepository;
import tn.esprit.dentalcare.plantraitement.repository.PlanTraitementRepository;
import tn.esprit.dentalcare.plantraitement.web.ApiException;

@Service
@Transactional
public class PlanTraitementService {

    private final PlanTraitementRepository planRepository;
    private final ActeCatalogueService catalogueService;
    private final FactureRepository factureRepository;

    public PlanTraitementService(
            PlanTraitementRepository planRepository,
            ActeCatalogueService catalogueService,
            FactureRepository factureRepository) {
        this.planRepository = planRepository;
        this.catalogueService = catalogueService;
        this.factureRepository = factureRepository;
    }

    public PlanResponse create(CreatePlanRequest request) {
        validateEtapes(request.etapes());

        PlanTraitement plan = new PlanTraitement();
        plan.setPatientId(request.patientId());
        plan.setStatut(StatutPlan.BROUILLON);

        BigDecimal total = BigDecimal.ZERO;
        for (EtapeRequest etapeRequest : sortedEtapes(request.etapes())) {
            ActeCatalogue acte = catalogueService.getEntity(etapeRequest.acteCode());
            EtapePlan etape = new EtapePlan();
            etape.setOrdre(etapeRequest.ordre());
            etape.setActeCode(acte.getCode());
            etape.setDent(etapeRequest.dent());
            etape.setStatut(StatutEtape.PLANIFIEE);
            plan.addEtape(etape);
            total = total.add(acte.getTarif());
        }

        Devis devis = new Devis();
        devis.setPlan(plan);
        devis.setMontantTotal(total);
        plan.setDevis(devis);

        buildEcheancier(plan, total);

        return PlanResponse.from(planRepository.save(plan));
    }

    @Transactional(readOnly = true)
    public PlanResponse getById(Long id) {
        return PlanResponse.from(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> findByPatient(String patientId) {
        return planRepository.findByPatientIdOrderByCreeLeDesc(patientId).stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> findAll() {
        return planRepository.findAll().stream().map(PlanResponse::from).toList();
    }

    public PlanResponse proposer(Long id) {
        PlanTraitement plan = getEntity(id);
        if (plan.getStatut() != StatutPlan.BROUILLON) {
            throw new ApiException(HttpStatus.CONFLICT, "Only BROUILLON plans can be proposed");
        }
        plan.setStatut(StatutPlan.PROPOSE);
        plan.setMisAJourLe(LocalDateTime.now());
        return PlanResponse.from(planRepository.save(plan));
    }

    /**
     * Accept quotation. Saga orchestration comes in S7; here we freeze the plan and mark schedule.
     */
    public PlanResponse accepter(Long id) {
        PlanTraitement plan = getEntity(id);
        if (plan.getStatut() != StatutPlan.PROPOSE) {
            throw new ApiException(HttpStatus.CONFLICT, "Only PROPOSE plans can be accepted");
        }
        plan.setStatut(StatutPlan.ACCEPTE);
        plan.setMisAJourLe(LocalDateTime.now());
        if (plan.getDevis() != null) {
            plan.getDevis().setAccepteLe(LocalDate.now());
        }
        return PlanResponse.from(planRepository.save(plan));
    }

    public PlanResponse demarrer(Long id) {
        PlanTraitement plan = getEntity(id);
        if (plan.getStatut() != StatutPlan.ACCEPTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Only ACCEPTE plans can start");
        }
        plan.setStatut(StatutPlan.EN_COURS);
        plan.setMisAJourLe(LocalDateTime.now());
        return PlanResponse.from(planRepository.save(plan));
    }

    public PlanResponse abandonner(Long id) {
        PlanTraitement plan = getEntity(id);
        if (plan.getStatut() == StatutPlan.TERMINE) {
            throw new ApiException(HttpStatus.CONFLICT, "Finished plans cannot be abandoned");
        }
        plan.setStatut(StatutPlan.ABANDONNE);
        plan.setMisAJourLe(LocalDateTime.now());
        return PlanResponse.from(planRepository.save(plan));
    }

    public FactureResponse realiserEtape(Long planId, Long etapeId) {
        PlanTraitement plan = getEntity(planId);
        if (plan.getStatut() != StatutPlan.EN_COURS && plan.getStatut() != StatutPlan.ACCEPTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Plan must be ACCEPTE or EN_COURS");
        }
        if (plan.getStatut() == StatutPlan.ACCEPTE) {
            plan.setStatut(StatutPlan.EN_COURS);
        }

        EtapePlan etape = plan.getEtapes().stream()
                .filter(e -> e.getId().equals(etapeId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Step not found: " + etapeId));

        if (etape.getStatut() == StatutEtape.REALISEE) {
            throw new ApiException(HttpStatus.CONFLICT, "Step already completed");
        }

        // Enforce order: previous steps must be completed
        boolean previousDone = plan.getEtapes().stream()
                .filter(e -> e.getOrdre() < etape.getOrdre())
                .allMatch(e -> e.getStatut() == StatutEtape.REALISEE);
        if (!previousDone) {
            throw new ApiException(HttpStatus.CONFLICT, "Previous steps must be completed first");
        }

        ActeCatalogue acte = catalogueService.getEntity(etape.getActeCode());
        etape.setStatut(StatutEtape.REALISEE);

        Facture facture = new Facture();
        facture.setPlanId(plan.getId());
        facture.setEtapeId(etape.getId());
        facture.setMontant(acte.getTarif());
        Facture saved = factureRepository.save(facture);

        boolean allDone = plan.getEtapes().stream().allMatch(e -> e.getStatut() == StatutEtape.REALISEE);
        if (allDone) {
            plan.setStatut(StatutPlan.TERMINE);
        }
        plan.setMisAJourLe(LocalDateTime.now());
        planRepository.save(plan);

        return FactureResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public DevisResponse getDevis(Long planId) {
        PlanTraitement plan = getEntity(planId);
        if (plan.getDevis() == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No quotation for plan " + planId);
        }
        return DevisResponse.from(plan.getDevis());
    }

    @Transactional(readOnly = true)
    public List<EcheanceResponse> getEcheancier(Long planId) {
        PlanTraitement plan = getEntity(planId);
        return plan.getEcheances().stream().map(EcheanceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<FactureResponse> getFactures(Long planId) {
        getEntity(planId);
        return factureRepository.findByPlanIdOrderByEmiseLeDesc(planId).stream()
                .map(FactureResponse::from)
                .toList();
    }

    public void delete(Long id) {
        PlanTraitement plan = getEntity(id);
        if (plan.getStatut() != StatutPlan.BROUILLON) {
            throw new ApiException(HttpStatus.CONFLICT, "Only BROUILLON plans can be deleted");
        }
        planRepository.delete(plan);
    }

    private PlanTraitement getEntity(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Plan not found: " + id));
    }

    private void validateEtapes(List<EtapeRequest> etapes) {
        Set<Integer> ordres = new HashSet<>();
        for (EtapeRequest etape : etapes) {
            if (!ordres.add(etape.ordre())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Duplicate step order: " + etape.ordre());
            }
            catalogueService.getEntity(etape.acteCode());
            if (etape.dent() != null && (etape.dent() < 11 || etape.dent() > 48)) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid FDI tooth number: " + etape.dent());
            }
        }
    }

    private List<EtapeRequest> sortedEtapes(List<EtapeRequest> etapes) {
        return etapes.stream().sorted(Comparator.comparing(EtapeRequest::ordre)).toList();
    }

    private void buildEcheancier(PlanTraitement plan, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal half = total.divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP);
        BigDecimal rest = total.subtract(half);

        Echeance first = new Echeance();
        first.setMontant(half);
        first.setDateLimite(LocalDate.now().plusDays(7));
        first.setReglee(false);
        plan.addEcheance(first);

        Echeance second = new Echeance();
        second.setMontant(rest);
        second.setDateLimite(LocalDate.now().plusDays(30));
        second.setReglee(false);
        plan.addEcheance(second);
    }
}
