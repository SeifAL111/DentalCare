package tn.esprit.dentalcare.plantraitement.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.dentalcare.plantraitement.dto.ActeCatalogueRequest;
import tn.esprit.dentalcare.plantraitement.dto.ActeCatalogueResponse;
import tn.esprit.dentalcare.plantraitement.service.ActeCatalogueService;

@RestController
@RequestMapping("/api/actes-catalogue")
@Tag(name = "Catalogue d'actes")
public class ActeCatalogueController {

    private final ActeCatalogueService service;

    public ActeCatalogueController(ActeCatalogueService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List procedure catalogue")
    public List<ActeCatalogueResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get one catalogue entry")
    public ActeCatalogueResponse findOne(@PathVariable String code) {
        return service.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create catalogue entry")
    public ActeCatalogueResponse create(@Valid @RequestBody ActeCatalogueRequest request) {
        return service.create(request);
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update catalogue entry")
    public ActeCatalogueResponse update(
            @PathVariable String code,
            @Valid @RequestBody ActeCatalogueRequest request) {
        return service.update(code, request);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete catalogue entry")
    public void delete(@PathVariable String code) {
        service.delete(code);
    }
}
