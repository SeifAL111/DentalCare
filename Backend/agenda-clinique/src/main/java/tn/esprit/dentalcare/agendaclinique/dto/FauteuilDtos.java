package tn.esprit.dentalcare.agendaclinique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.esprit.dentalcare.agendaclinique.domain.Fauteuil;

public final class FauteuilDtos {

    private FauteuilDtos() {
    }

    public record FauteuilRequest(
            @NotBlank @Size(max = 20) String numero,
            @NotBlank @Size(max = 50) String salle) {
    }

    public record FauteuilResponse(Long id, String numero, String salle, boolean actif) {

        public static FauteuilResponse from(Fauteuil f) {
            return new FauteuilResponse(f.getId(), f.getNumero(), f.getSalle(), f.isActif());
        }
    }
}
