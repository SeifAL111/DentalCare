package tn.esprit.dentalcare.dossiermedical.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dossierMedicalOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DentalCare — Dossier Médical")
                .version("v1")
                .description("""
                        Contexte du risque médical du patient : allergies, antécédents, consentements, \
                        partage temporaire et journal d'accès.

                        Les autres microservices interrogent ce service au moment de décider \
                        (contre-indications, facteurs de risque, consentement avant acte invasif). \
                        Il ne lit aucune autre base et ne stocke que des identifiants externes.

                        Données de démonstration au démarrage : patient-ahmed, patient-elodie-mercier, \
                        patient-camille-dubois, patient-paul-henry, patient-julien-roux."""));
    }
}
