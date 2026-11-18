# Technology decisions

## Current foundation (S5)

| Layer | Choice | Version |
|---|---|---|
| Java | 17 | 17 |
| Spring Boot | parent POM | 4.1.2 |
| Spring Cloud | Oakwood | 2025.1.4 |
| Discovery | Eureka | via Spring Cloud |
| Gateway | Spring Cloud Gateway (WebMVC) | via Spring Cloud |
| API docs | springdoc-openapi | 3.2.0 |

## Planned later (not in S5 code)

- S6: OpenFeign
- S7: RabbitMQ
- S8: Keycloak
- S9: Docker Compose full stack
