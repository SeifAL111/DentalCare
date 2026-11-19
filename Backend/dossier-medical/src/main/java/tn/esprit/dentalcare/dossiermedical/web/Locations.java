package tn.esprit.dentalcare.dossiermedical.web;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/** En-tête Location des réponses 201 : l'URL de la ressource créée. */
final class Locations {

    private Locations() {
    }

    static URI de(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
