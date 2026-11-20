package tn.esprit.dentalcare.suivi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger / OpenAPI pour documenter tous les endpoints du microservice.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI suiviOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DentalCare - API Suivi Post-opératoire")
                        .description("Microservice n°6 : Surveillance post-opératoire des patients, protocoles, réponses et alertes.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Youssef Ayachi")
                                .email("youssef.ayachi@esprit.tn")));
    }
}
