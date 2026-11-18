# DentalCare

Plateforme d'orchestration des parcours de soins dentaires — architecture microservices (module AWD).

## Équipe

* Nour El Houda Trabelsi — Agenda Clinique
* Arij Yousfi — Dossier Médical
* Seifeddine Alaimi — Laboratoire & Prothèses
* Ahmed Bejaoui — Plan de Traitement & Devis
* Ahmed Guesmi — Odontogramme & Actes
* Youssef Ayachi — Suivi Post-opératoire

## Architecture (rappel)

7 microservices métier + Gateway + Eureka (+ Config Server prévu dans la structure).

## Focus actuel — Séance 5

Selon le planning du mini-projet :

> **S5 · API Gateway** (routes vers les services + Eureka)

| À faire en S5 | Statut |
|---|---|
| Eureka Server | Prêt (`Backend/eureka-server`, port 8761) |
| API Gateway | Prêt (`Backend/api-gateway`, port 8080) |
| Enregistrement des MS dans Eureka | Configuré |
| Routage Gateway → services | Configuré |

**Pas encore (séances suivantes) :** OpenFeign (S6), RabbitMQ (S7), Keycloak (S8), Dockerisation complète (S9).

## Structure

```text
dentalcare/
├── Backend/
│   ├── dossier-medical/
│   ├── plan-traitement/
│   ├── agenda-clinique/
│   ├── odontogramme-actes/
│   ├── laboratoire-protheses/
│   ├── suivi-postoperatoire/
│   ├── coordination-intelligente/
│   ├── api-gateway/
│   ├── eureka-server/
│   └── config-server/
├── Frontend/
├── Documentation/
├── docker-compose.yml          # placeholder (S9)
└── README.md
```

## Versions

- Java 17
- Spring Boot 4.1.2
- Spring Cloud 2025.1.4

## Lancement Séance 5

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# 1. Eureka
cd Backend/eureka-server && mvn spring-boot:run

# 2. Un ou plusieurs MS (ex. H2 — démarrent sans Docker)
cd Backend/dossier-medical && mvn spring-boot:run
cd Backend/plan-traitement && mvn spring-boot:run
cd Backend/agenda-clinique && mvn spring-boot:run

# 3. API Gateway
cd Backend/api-gateway && mvn spring-boot:run
```

Ou :

```bash
./scripts/start-s5.sh
```

### Vérifications

- Eureka UI : http://localhost:8761
- Gateway health : http://localhost:8080/actuator/health
- Swagger (exemple) : http://localhost:8081/swagger-ui/index.html
- Route via Gateway (exemple) : http://localhost:8080/api/dossiers/...

## Guide équipe (comment travailler)

Voir le guide professionnel par membre :

`Documentation/Technical/TEAM_WORK_GUIDE.md`

## Ports S5

| Service | Port |
|---|---:|
| API Gateway | 8080 |
| Eureka | 8761 |
| Dossier Médical | 8081 |
| Plan de Traitement | 8082 |
| Agenda Clinique | 8083 |
| Odontogramme & Actes | 8084 |
| Laboratoire & Prothèses | 8085 |
| Suivi Post-opératoire | 8086 |
| Coordination Intelligente | 8087 |
| Config Server | 8888 |
