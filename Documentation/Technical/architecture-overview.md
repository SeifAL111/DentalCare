# Architecture overview

See also the Séance 4 dossier technique and presentation PDFs.

## Séance 5 slice

```text
Client → API Gateway (:8080) → Eureka (:8761) → Microservices
```

- Synchronous REST entry only via the Gateway
- Service discovery via Eureka (`lb://service-name`)

## Later sessions (not implemented yet)

- S6 OpenFeign between services
- S7 RabbitMQ events / Saga
- S8 Keycloak
- S9 Docker images for all services
