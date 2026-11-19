package tn.esprit.dentalcare.agendaclinique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import tn.esprit.dentalcare.agendaclinique.domain.Disponibilite;

public final class DisponibiliteDtos {

    private DisponibiliteDtos() {
    }

    /** Exemple : {"praticienId":"PRAT-001","jour":"MONDAY","debut":"08:30","fin":"12:30"} */
    public record DisponibiliteRequest(
            @NotBlank String praticienId,
            @NotNull DayOfWeek jour,
            @NotNull LocalTime debut,
            @NotNull LocalTime fin) {
    }

    public record DisponibiliteResponse(Long id, String praticienId, DayOfWeek jour, LocalTime debut, LocalTime fin) {

        public static DisponibiliteResponse from(Disponibilite d) {
            return new DisponibiliteResponse(d.getId(), d.getPraticienId(), d.getJour(), d.getDebut(), d.getFin());
        }
    }
}
