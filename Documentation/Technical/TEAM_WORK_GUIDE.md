# DentalCare — Team Work Guide (Professional Workflow)

This guide tells **each teammate exactly how to work** on their microservice so the project stays clean, consistent, and demo-ready.

---

## 1. Shared rules (everyone)

### Do
- Work **only** in your service folder: `Backend/<your-service>/`
- Create a branch: `feature/<service>/<short-task>`
- Commit often with clear messages (`feat:`, `fix:`, `docs:`)
- Keep Swagger up to date (springdoc is already configured)
- Register your service in Eureka and verify it appears at http://localhost:8761
- Expose APIs under the paths already routed by the Gateway
- Use **Database-per-Service**: never read another service’s database
- Store only IDs for other contexts (`patientId`, Keycloak id, `planId`, …)

### Don’t
- Don’t push secrets (`.env`, passwords)
- Don’t implement Keycloak / RabbitMQ / OpenFeign until the matching séance
- Don’t change Gateway routes without telling the group
- Don’t put business logic of another microservice in your project

### Recommended package layout (same for all MS)

```text
tn.esprit.dentalcare.<service>/
├── domain/          # entities + enums
├── repository/      # Spring Data JPA
├── dto/             # request/response objects
├── service/         # business rules
├── web/             # REST controllers
└── config/          # OpenAPI, CORS if needed
```

### Daily checklist
1. `git pull`
2. Start Eureka (if not running)
3. Start **your** service
4. Check Eureka registration
5. Develop + test in Swagger
6. Commit + push your branch

### How to run your service

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
# Eureka must be running on 8761
cd Backend/<your-service>
mvn spring-boot:run
```

Swagger: `http://localhost:<your-port>/swagger-ui/index.html`

---

## 2. Individual guides

---

### Arij Yousfi — `dossier-medical` (port 8081, H2)

**Mission:** Own the patient medical risk context (history, allergies, consents).

**Priority order (pro path)**
1. Entity `DossierMedical` (`patientId`, creation date)
2. Related entities: `Allergie`, `Antecedent`, `Consentement`, `PartageTemporaire`, `JournalAcces`
3. CRUD for dossier + nested resources
4. Business rules:
   - allergy blocks incompatible prescription
   - consent required before invasive act
   - temporary share with access log
5. Endpoints (from conception):
   - `POST /api/dossiers`
   - `GET /api/dossiers/{id}`
   - `POST /api/dossiers/{id}/allergies`
   - `POST /api/dossiers/{id}/consentements`
   - `POST /api/dossiers/{id}/partages`
   - `POST /api/dossiers/{id}/contre-indications/verifier`
   - `GET /api/dossiers/{id}/facteurs-risque`
6. Return proper HTTP codes (`201`, `400`, `404`, `403`)

**Quality bar**
- Validation annotations on DTOs
- Clear error messages
- No dependency on other DBs

**Later**
- Called by Odontogramme and Suivi via OpenFeign (S6)

---

### Ahmed Bejaoui — `plan-traitement` (port 8082, H2)

**Mission:** Own treatment journey, quotation, invoice schedule; later Saga driver.

**Priority order**
1. `ActeCatalogue` (code, duration, tariff, healing delay, required skill)
2. `PlanTraitement` + `EtapePlan` (ordered steps)
3. `Devis`, `Echeance`, `Facture`
4. Status machine: `BROUILLON → PROPOSÉ → ACCEPTÉ → EN_COURS → TERMINÉ/ABANDONNÉ`
5. Endpoints:
   - `GET /api/actes-catalogue`
   - `POST /api/plans`
   - `GET /api/plans/{id}`
   - `PATCH /api/plans/{id}/accepter` (for now: change status only; Saga in S7)
   - `GET /api/plans/{id}/devis`
   - `GET /api/plans/{id}/echeancier`
6. Rules:
   - steps ordered with minimum delay
   - plan frozen after accepted quote
   - one invoice per completed act (can be stubbed first)

**Quality bar**
- Explicit enums for statuses
- Reject illegal transitions with `409`

**Later**
- OpenFeign to Odontogramme (S6)
- Publish `PlanAccepte` on RabbitMQ (S7)

---

### Nour El Houda Trabelsi — `agenda-clinique` (port 8083, H2)

**Mission:** Allocate practitioners, chairs, slots, waiting list.

**Priority order**
1. Entities: `Praticien`, `Fauteuil`, `RendezVous`, `ListeAttente`, `OffreCreneau`
2. CRUD for practitioners/chairs first (easy wins)
3. Appointment booking with conflict checks
4. Status: `CONFIRMÉ → DÉPLACÉ/ANNULÉ/TERMINÉ/ABSENT`
5. Endpoints:
   - `GET /api/creneaux?acte=&praticien=`
   - `POST /api/rdv`
   - `PATCH /api/rdv/{id}/annuler`
   - `POST /api/liste-attente`
   - `POST /api/offres/{id}/accepter`
6. Rules:
   - no double booking (transaction + unique constraint/lock)
   - cancelled slot offered to waiting list (timer can be simplified first)

**Quality bar**
- DB constraint against overlapping RDV
- Tests for double-booking attempt

**Later**
- OpenFeign to Plan for act duration (S6)
- Publish `CreneauLibere` (S7)

---

### Ahmed Guesmi — `odontogramme-actes` (port 8084, MySQL)

**Mission:** Immutable tooth history (FDI 11–48) and clinical acts.

**Setup first**
1. Start MySQL
2. Create DB `odontogramme_actes`
3. Align credentials with `application.properties` / env vars
4. Confirm service starts and appears in Eureka

**Priority order**
1. Entity `EvenementDentaire` (immutable: no update/delete API)
2. Reconstruct current tooth state from events
3. `Acte` and `Ordonnance`
4. Endpoints:
   - `POST /api/actes`
   - `POST /api/ordonnances`
   - `GET /api/odontogrammes/{patientId}?date=`
   - `GET /api/odontogrammes/{patientId}/dents/{n}/historique`
5. Rules:
   - refuse incompatible act (e.g. filling on missing tooth)
   - FDI validation `11–18, 21–28, 31–38, 41–48`
   - link act to plan step id (store id only)

**Quality bar**
- Event sourcing style: append-only history
- No `PUT` that overwrites past tooth state

**Later**
- OpenFeign to Dossier for contraindication check (S6)
- Publish `ActeRealise` (S7)

---

### Seifeddine Alaimi — `laboratoire-protheses` (port 8085, PostgreSQL)

**Mission:** Prosthesis manufacturing workflow and delay detection.

**Setup first**
1. Start PostgreSQL
2. Create DB `laboratoire_protheses`
3. Confirm service starts + Eureka registration

**Priority order**
1. Entities: `Laboratoire`, `CommandeProthese`, `Retouche`
2. Status machine:  
   `EN_ATTENTE → ACCEPTÉE → EN_FABRICATION → CONTRÔLE → PRÊTE → LIVRÉE → POSÉE` (+ `RETOUCHE`)
3. Endpoints:
   - `POST /api/protheses`
   - `GET /api/protheses/{id}`
   - `PATCH /api/protheses/{id}/statut`
   - `POST /api/protheses/{id}/retouches`
   - `GET /api/laboratoires/{id}/commandes`
4. Rules:
   - allow only valid transitions
   - promised date + simple overdue query
   - lab user sees only their lab orders (filter by `laboratoireId`; Keycloak later)

**Quality bar**
- Transition guard in service layer
- Index on status + promised date

**Later**
- Publish `ProthesePrete` / `ProtheseEnRetard` (S7)

---

### Youssef Ayachi — `suivi-postoperatoire` (port 8086, MySQL)

**Mission:** Post-op protocols, patient follow-up, alerts.

**Setup first**
1. Start MySQL
2. Create DB `suivi_postoperatoire`
3. Confirm Eureka registration

**Priority order**
1. Entities: `Protocole`, `Suivi`, `ReponsePatient`, `Alerte`
2. Status: `ACTIF → EN_ALERTE → PRIS_EN_CHARGE → CLÔTURÉ` (+ `SANS_RÉPONSE`)
3. Endpoints:
   - `GET/POST /api/protocoles`
   - `POST /api/suivis` (open follow-up)
   - `GET /api/suivis/patient/{id}`
   - `GET /api/suivis/{id}/consignes`
   - `POST /api/suivis/{id}/reponses`
   - `GET /api/suivis/alertes`
   - `PATCH /api/suivis/{id}/cloturer`
4. Rules:
   - open follow-up after invasive act (can be manual trigger first)
   - threshold → alert (e.g. pain >= 7)
   - only dentist closes follow-up (enforce later with Keycloak)

**Quality bar**
- Protocol-driven questions/thresholds
- Alert list query for assistant dashboard

**Later**
- OpenFeign to Dossier for risk factors (S6)
- Publish `ComplicationSuspectee` / `PatientSansReponse` / `CicatrisationConfirmee` (S7)

---

### Whole team — `coordination-intelligente` (port 8087, PostgreSQL + pgvector later)

**Mission:** Shared AI coordination service (advanced MS). Do this **after** core CRUD of the 6 MS is usable.

**Phase A (now / soon)**
1. Entities: `Conversation`, `Message`, `Proposition`, `EntreeAudit`
2. CRUD for propositions:
   - `GET /api/propositions?statut=EN_ATTENTE`
   - `PATCH /api/propositions/{id}/approuver`
   - `GET /api/audit`
3. Manual proposition creation first (no LLM yet)

**Phase B (later)**
- Spring AI + Ollama
- pgvector knowledge base
- Agents Secrétaire / Clinique / Laboratoire

---

### Whole team — Infrastructure (S5 focus)

| Component | Owner | Task |
|---|---|---|
| `eureka-server` | Groupe | Keep running; verify all MS register |
| `api-gateway` | Groupe | Keep routes aligned with real controller paths |
| `config-server` | Groupe | Leave minimal for now; deepen later if needed |
| `Frontend` | Groupe | Wait for stable APIs; then consume Gateway only |

---

## 3. Definition of Done (per microservice)

A teammate’s MS is “pro-ready” when:
- [ ] Service starts with `mvn spring-boot:run`
- [ ] Visible in Eureka as `UP`
- [ ] Main entities + repositories done
- [ ] Main endpoints work in Swagger
- [ ] Validation + correct HTTP status codes
- [ ] README of the service updated (how to run + main APIs)
- [ ] Personal commits on GitHub

---

## 4. Suggested 2-week rhythm

| Days | Focus |
|---|---|
| 1–2 | Run service + Eureka + first entity/CRUD |
| 3–5 | Core endpoints from conception PDF |
| 6–7 | Business rules + status transitions |
| 8+ | Polish Swagger examples, edge cases, seed data |

Séance mapping reminder:
- **S5** Gateway + Eureka  
- **S6** OpenFeign  
- **S7** RabbitMQ / Saga  
- **S8** Keycloak  
- **S9** Docker  

---

## 5. Communication tip

When you finish an endpoint, post in the team chat:

```text
[service] METHOD /api/... → status codes
Example body: {...}
Swagger: http://localhost:PORT/swagger-ui/index.html
```

This helps Gateway/Frontend stay aligned.
