# agenda-clinique

- **Responsabilité :** allouer praticiens, fauteuils et créneaux ; liste d'attente
- **Responsable :** Nour El Houda Trabelsi
- **Port :** 8083 · **Base :** H2 (en mémoire) · **Nom Eureka :** `agenda-clinique`
- **Stack :** Spring Boot 4.1.2 · Spring Cloud 2025.1.4 · Java 17

## Lancer

Eureka (8761) d'abord, puis :

```bash
cd Backend/agenda-clinique
./mvnw spring-boot:run          # Windows : mvnw.cmd spring-boot:run
```

- Swagger : http://localhost:8083/swagger-ui/index.html
- Console H2 : http://localhost:8083/h2-console (JDBC URL `jdbc:h2:mem:agenda_clinique`, user `sa`, sans mot de passe)
- Via le Gateway : http://localhost:8080/api/rdv

Au démarrage, des données fictives sont créées : fauteuils **F1, F2** (Salle 1) et **F3** (Salle 2) ;
**PRAT-001** du lundi au vendredi 08:30-12:30 et 14:00-18:00 ; **PRAT-002** du lundi au samedi 09:00-13:00.

## API REST

| Méthode | Chemin | Rôle | Codes |
|---|---|---|---|
| GET | `/api/creneaux?acte=&praticien=&date=&jours=` | Créneaux compatibles avec un acte | 200, 400 |
| POST | `/api/rdv` | Réserver un créneau | 201, 400, 404, 409 |
| GET | `/api/rdv?patientId=&praticien=&date=` · `/api/rdv/{id}` | Consulter | 200, 404 |
| PATCH | `/api/rdv/{id}/annuler` | Annuler et libérer le créneau (offert à la liste d'attente) | 200, 404, 409 |
| PATCH | `/api/rdv/{id}/deplacer` | Déplacer (ancien → `DEPLACE`, nouveau `CONFIRME`) | 200, 400, 404, 409 |
| PATCH | `/api/rdv/{id}/terminer` · `/api/rdv/{id}/absent` | Clôturer | 200, 404, 409 |
| POST | `/api/rdv/urgence` | Premier créneau libre dans les 3 jours | 201, 409 |
| POST | `/api/liste-attente` | S'inscrire en liste d'attente | 201, 400, 409 |
| GET · DELETE | `/api/liste-attente` · `/api/liste-attente/{id}` | Consulter · se retirer | 200, 404, 409 |
| GET | `/api/offres?patientId=&statut=` | Offres de créneau | 200 |
| POST | `/api/offres/{id}/accepter` | Accepter une offre → rendez-vous créé | 200, 404, 409, **410** |
| POST | `/api/offres/{id}/refuser` | Refuser → offre au patient suivant | 200, 404, 409 |
| POST | `/api/offres/verifier-expirations` | Forcer la vérification des offres expirées | 200 |
| GET · POST · PATCH | `/api/fauteuils` · `/api/fauteuils/{id}/hors-service` | Fauteuils | 200, 201, 404, 409 |
| GET · POST · DELETE | `/api/disponibilites` | Horaires des praticiens | 200, 201, 204, 400, 409 |

`409` : créneau, fauteuil ou praticien déjà pris · `410` : offre expirée après 15 minutes.
Erreurs au format `{"timestamp","status","error","message"}`.

## Règles métier et où elles sont codées

| Règle | Implémentation |
|---|---|
| Un créneau exige un praticien disponible, un fauteuil libre et la durée de l'acte | `ReservationEngine.reserver` : plage `Disponibilite` qui couvre le créneau, fauteuil actif, durée lue dans `CatalogueActes` |
| Pas de double réservation | Verrouillage transactionnel `SELECT ... FOR UPDATE` (plages du praticien ce jour-là, puis fauteuil), puis contrôle des chevauchements fauteuil / praticien / patient. Testé avec deux réservations simultanées. |
| Créneau annulé offert 15 min à la liste d'attente, puis au suivant | `ListeAttenteService.proposerCreneauLibere` (premier inscrit compatible) ; `OffreExpirationJob` chaque minute ; `410` si on accepte trop tard |
| Créneau d'urgence si complication | `POST /api/rdv/urgence` (déclenché par l'événement `ComplicationSuspectee` en S7) |

Contexte borné : `patientId`, `praticienId` (identifiant Keycloak) et `etapePlanId` sont de simples identifiants,
jamais les tables d'un autre service.

## Démo via le Gateway (port 8080)

Remplacer `2026-11-23` par un lundi à venir.

```bash
# 1. Créneaux de 60 min pour une dévitalisation avec PRAT-001
curl "http://localhost:8080/api/creneaux?acte=DEVIT&praticien=PRAT-001&date=2026-11-23&jours=1"

# 2. Ahmed réserve 09:00 sur F1 -> 201
curl -X POST http://localhost:8080/api/rdv -H "Content-Type: application/json" \
  -d '{"patientId":"PAT-AHMED","praticienId":"PRAT-001","fauteuilId":1,"acteCode":"DEVIT","debut":"2026-11-23T09:00:00"}'

# 3. Même fauteuil, même heure -> 409 (pas de double réservation)
curl -X POST http://localhost:8080/api/rdv -H "Content-Type: application/json" \
  -d '{"patientId":"PAT-SARA","praticienId":"PRAT-002","fauteuilId":1,"acteCode":"DETARTRAGE","debut":"2026-11-23T09:30:00"}'

# 4. Sara s'inscrit en liste d'attente, Ahmed annule -> une offre de 15 min est créée pour Sara
curl -X POST http://localhost:8080/api/liste-attente -H "Content-Type: application/json" \
  -d '{"patientId":"PAT-SARA","acteCode":"DETARTRAGE","praticienId":"PRAT-001"}'
curl -X PATCH http://localhost:8080/api/rdv/1/annuler
curl "http://localhost:8080/api/offres?patientId=PAT-SARA"

# 5. Sara accepte -> 200 et nouveau rendez-vous
curl -X POST http://localhost:8080/api/offres/1/accepter
```

## Tests

```bash
./mvnw test
```

`AgendaCliniqueIntegrationTest` : 409 fauteuil et praticien, 400 hors disponibilité, deux réservations
simultanées (une seule réussit), annulation → offre → acceptation, offre expirée → 410 et patient suivant,
recherche de créneaux, urgence.

## Séances suivantes

- **S6 · OpenFeign** : `CatalogueActes` appellera plan-traitement (`GET /api/actes-catalogue/{code}`) ;
  la copie locale `CatalogueActesLocal` reste le cache de repli (sinon `503`, l'agenda reste consultable).
- **S7 · RabbitMQ** : publier `CreneauLibere` (dans `proposerCreneauLibere`) ; consommer `PlanAccepte`
  (planifier les séances), `ProthesePrete` (rendez-vous de pose), `ComplicationSuspectee` (urgence).
- **S8 · Keycloak** : `praticienId` / `patientId` = identifiants Keycloak, droits par rôle.
