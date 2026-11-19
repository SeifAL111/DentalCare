package tn.esprit.dentalcare.plantraitement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI planTraitementOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DentalCare — Plan de Traitement & Devis")
                .description("Microservice owned by Ahmed Bejaoui. H2 database. Eureka-enabled.")
                .version("1.0.0")
                .contact(new Contact().name("Ahmed Bejaoui")));
    }
}
