# Séance 5 — API Gateway + Eureka

Source: *Presentation_MiniProjet_AWD_2026_2027* — planning incrémental.

## Objectif S5

- Démarrer **Eureka**
- Enregistrer les microservices
- Démarrer **Spring Cloud Gateway**
- Router les requêtes front → Gateway → microservices

## Hors périmètre S5

| Séance | Sujet |
|---|---|
| S6 | Communication synchrone (OpenFeign) |
| S7 | Communication asynchrone (RabbitMQ) |
| S8 | Sécurité Keycloak |
| S9 | Dockerisation |

## Services utiles pour la démo S5

1. `eureka-server` (8761)
2. Au moins un MS métier (ex. `dossier-medical` H2, 8081)
3. `api-gateway` (8080)

Les services MySQL/PostgreSQL peuvent rester dans le dépôt ; ils ne sont pas nécessaires pour prouver le Gateway en S5.

## Routes Gateway (préparées)

| Path | Service Eureka |
|---|---|
| `/api/dossiers/**` | dossier-medical |
| `/api/plans/**`, `/api/actes-catalogue/**` | plan-traitement |
| `/api/rdv/**`, `/api/creneaux/**`, … | agenda-clinique |
| `/api/actes/**`, `/api/odontogrammes/**`, … | odontogramme-actes |
| `/api/protheses/**`, … | laboratoire-protheses |
| `/api/protocoles/**`, `/api/suivis/**` | suivi-postoperatoire |
| `/api/assistant/**`, `/api/propositions/**`, … | coordination-intelligente |
