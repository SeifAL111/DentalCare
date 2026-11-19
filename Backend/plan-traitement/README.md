# plan-traitement

- **Owner:** Ahmed Bejaoui
- **Port:** 8082
- **Database:** H2
- **Stack:** Spring Boot 4.1.2 · Spring Cloud 2025.1.4 · Java 17

## Run

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

## Swagger

http://localhost:8082/swagger-ui/index.html

## Main APIs

| Method | Path | Description |
|---|---|---|
| GET/POST/PUT/DELETE | `/api/actes-catalogue` | Procedure catalogue CRUD |
| POST | `/api/plans` | Create draft plan |
| GET | `/api/plans` | List plans (`?patientId=`) |
| GET | `/api/plans/{id}` | Get plan |
| PATCH | `/api/plans/{id}/proposer` | BROUILLON → PROPOSE |
| PATCH | `/api/plans/{id}/accepter` | PROPOSE → ACCEPTE |
| PATCH | `/api/plans/{id}/demarrer` | ACCEPTE → EN_COURS |
| POST | `/api/plans/{id}/etapes/{etapeId}/realiser` | Complete step + invoice |
| GET | `/api/plans/{id}/devis` | Quotation |
| GET | `/api/plans/{id}/echeancier` | Payment schedule |
| GET | `/api/plans/{id}/factures` | Invoices |
