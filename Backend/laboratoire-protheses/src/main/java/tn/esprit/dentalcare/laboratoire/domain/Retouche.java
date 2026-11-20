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
public class Retouche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String motif;
    private LocalDate dateDemandeRetouche;

    @ManyToOne
    @JoinColumn(name = "commande_prothese_id")
    private CommandeProthese commandeProthese;
}