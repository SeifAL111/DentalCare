package tn.esprit.dentalcare.agendaclinique.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI agendaCliniqueOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DentalCare — Agenda Clinique")
                .description("Praticiens, fauteuils, créneaux, rendez-vous et liste d'attente. "
                        + "Base H2, enregistré dans Eureka, exposé via l'API Gateway (port 8080).")
                .version("1.0.0")
                .contact(new Contact().name("Nour El Houda Trabelsi")));
    }
}
