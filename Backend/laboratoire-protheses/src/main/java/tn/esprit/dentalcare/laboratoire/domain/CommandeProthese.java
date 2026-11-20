package tn.esprit.dentalcare.laboratoire.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeProthese {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId; // Référence externe stockée uniquement par ID (règle inter-services)

    private String typeProthese;

    @Enumerated(EnumType.STRING)
    private StatutProthese statut;

    private LocalDate dateDemande;
    private LocalDate datePromesse; // Nécessaire pour la détection de retard

    @ManyToOne
    @JoinColumn(name = "laboratoire_id")
    private Laboratoire laboratoire;
}