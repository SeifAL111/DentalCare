# dossier-medical

- **Responsabilité :** contexte du risque médical du patient (allergies, antécédents, consentements, partage)
- **Owner :** Arij Yousfi
- **Port :** 8081
- **Base :** H2 (en mémoire, mode PostgreSQL)
- **Stack :** Spring Boot 4.1.2 · Spring Cloud 2025.1.4 · Java 17

## Lancer

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || echo /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home)
# Eureka doit tourner sur 8761
mvn spring-boot:run
```

- Swagger : http://localhost:8081/swagger-ui.html
- Console H2 : http://localhost:8081/h2-console (JDBC URL `jdbc:h2:mem:dossier_medical`, user `sa`, sans mot de passe)
- Via la Gateway : http://localhost:8080/api/dossiers

## Tests

```bash
mvn test
```

- `MoteurContreIndicationsTest`, `EvaluateurRisqueTest` : règles métier, sans Spring ni base.
- `DossierMedicalApiTest` : endpoints de bout en bout (MockMvc, base H2 dédiée, sans Eureka).

## Organisation du code

```
tn.esprit.dentalcare.dossiermedical/
├── domain/       entités JPA + énumérations métier (TypeActe, FacteurRisque, FamilleMedicament…)
├── repository/   Spring Data JPA
├── dto/          records request/response avec validation
├── service/      règles métier (MoteurContreIndications, EvaluateurRisque) + services CRUD
├── web/          controllers REST + GestionnaireErreurs (ProblemDetail)
└── config/       OpenAPI + données de démonstration
```

## API

Toutes les routes sont sous `/api/dossiers/**`, la route Gateway existante.

### Dossiers

| Méthode | Chemin | Codes |
|---|---|---|
| POST | `/api/dossiers` | 201 · 400 · 409 (le patient a déjà un dossier) |
| GET | `/api/dossiers?recherche=&page=&taille=` | 200 |
| GET | `/api/dossiers/{id}` | 200 · 404 |
| GET | `/api/dossiers/patient/{patientId}` | 200 · 404 |
| PUT | `/api/dossiers/{id}` (avec `version`) | 200 · 400 · 404 · 409 (version obsolète) |
| DELETE | `/api/dossiers/{id}` (archivage) | 204 · 404 |
| PATCH | `/api/dossiers/{id}/declarer-aucune-allergie` | 200 · 404 · 409 |

### Allergies et antécédents

| Méthode | Chemin | Codes |
|---|---|---|
| POST | `/api/dossiers/{id}/allergies` | 201 · 400 · 404 |
| GET | `/api/dossiers/{id}/allergies` | 200 · 404 |
| GET · PUT · DELETE | `/api/dossiers/{id}/allergies/{allergieId}` | 200/204 · 400 · 404 |
| POST | `/api/dossiers/{id}/antecedents` | 201 · 400 · 404 |
| GET | `/api/dossiers/{id}/antecedents` | 200 · 404 |
| GET · PUT · DELETE | `/api/dossiers/{id}/antecedents/{antecedentId}` | 200/204 · 400 · 404 |

### Consentements (pas de PUT ni de DELETE : un document signé ne se modifie pas)

| Méthode | Chemin | Codes |
|---|---|---|
| POST | `/api/dossiers/{id}/consentements` | 201 · 400 · 404 · 409 (déjà valide) |
| GET | `/api/dossiers/{id}/consentements?typeActe=` | 200 · 404 |
| GET | `/api/dossiers/{id}/consentements/{cid}` | 200 · 404 |
| PATCH | `/api/dossiers/{id}/consentements/{cid}/revoquer` | 200 · 404 · 409 (déjà révoqué) |
| GET | `/api/dossiers/{id}/consentements/{cid}/integrite` | 200 · 404 |
| GET | `/api/dossiers/{id}/consentements/verifier?acte=` | 200 · 400 · 404 |

### Contrôles médicaux (appelés par les autres services en S6)

| Méthode | Chemin | Appelant |
|---|---|---|
| POST | `/api/dossiers/{id}/contre-indications/verifier` | Front, agent Clinique |
| POST | `/api/dossiers/patient/{patientId}/contre-indications/verifier` | Odontogramme & Actes |
| GET | `/api/dossiers/{id}/facteurs-risque?acte=` | Front |
| GET | `/api/dossiers/patient/{patientId}/facteurs-risque?acte=` | Suivi Post-opératoire |
| GET | `/api/dossiers/patient/{patientId}/consentements/verifier?acte=` | Odontogramme & Actes |

### Partage temporaire et journal

| Méthode | Chemin | Codes |
|---|---|---|
| POST | `/api/dossiers/{id}/partages` | 201 · 400 · 404 (403 avec Keycloak en S8) |
| GET | `/api/dossiers/{id}/partages` | 200 · 404 |
| DELETE | `/api/dossiers/{id}/partages/{partageId}` (révocation) | 204 · 404 |
| GET | `/api/dossiers/partages/{token}?praticienId=` | 200 · 403 · 404 · 410 |
| GET | `/api/dossiers/{id}/journal-acces` | 200 · 404 |

Toutes les erreurs suivent le format **ProblemDetail** (RFC 9457) ; les erreurs de validation ajoutent un champ `erreurs` (un message par champ).

## Règles métier

1. **Contre-indications sur 3 niveaux** : `OK`, `AVERTISSEMENT` ou `BLOQUANT`, avec les raisons en clair.
   - Allergie à la même famille de molécules (Pénicilline → Amoxicilline) : BLOQUANT (AVERTISSEMENT si la réaction était légère).
   - Réaction croisée (pénicillines ↔ céphalosporines) : AVERTISSEMENT.
   - Interaction avec un traitement en cours (anticoagulant + AINS, grossesse + AINS…) : AVERTISSEMENT.
   - Allergie aux sulfites : les anesthésiques adrénalinés sont BLOQUÉS (les sulfites y servent d'antioxydant).
2. **« Aucune allergie connue » n'est pas « non renseigné »** : tant que le praticien n'a pas vérifié, toute prescription donne un avertissement.
3. **Facteurs de risque selon l'acte** : un anticoagulant compte pour une extraction, pas pour un détartrage. Le niveau global est VERT / ORANGE / ROUGE, et `protocoleRenforce` indique au Suivi d'appliquer le protocole renforcé.
4. **Consentement obligatoire avant tout acte invasif** (dévitalisation, surfaçage, extraction, implant, greffe). Chaque consentement porte une empreinte SHA-256 qui prouve que le texte n'a pas changé, une date d'expiration (`dossier.consentement.validite-mois`) et peut être révoqué.
5. **Partage temporaire** : jeton aléatoire, 1 à 72 h, portée limitée (`ALLERGIES`, `RESUME_MEDICAL`, `COMPLET`), nombre de consultations optionnel. Chaque ouverture est inscrite au journal d'accès, qui est en lecture seule.
6. **Protection des données** : une allergie, un antécédent, un consentement ou un partage n'est accessible que par l'URL de son propre dossier ; un dossier s'archive, il ne se supprime pas ; `@Version` empêche deux praticiens de s'écraser.

Les codes de `TypeActe` doivent rester alignés avec l'`ActeCatalogue` du Plan de Traitement.
Les règles médicales sont des exemples de démonstration, à faire valider par un praticien.

## Données de démonstration

Chargées au démarrage si la base est vide (`dossier.seed.enabled=false` pour les désactiver) :

| patientId | Situation | À montrer |
|---|---|---|
| `patient-ahmed` | Allergie pénicilline (anaphylaxie), consentement dévitalisation | Amoxicilline → BLOQUANT |
| `patient-elodie-mercier` | Sous apixaban (anticoagulant), aucune allergie connue | Extraction → ROUGE ; ibuprofène → interaction |
| `patient-camille-dubois` | Allergie latex, consentement implant | Matériau dentaire |
| `patient-paul-henry` | Fumeur, diabète déséquilibré, allergies jamais vérifiées | Implant → ORANGE ; toute prescription → avertissement |
| `patient-julien-roux` | Allergie aux sulfites (sévère) | Articaïne adrénaline → BLOQUANT |

Les identifiants patients sont à aligner avec les données de démo des autres services.

## Évolutions prévues

- **S6** : les appels OpenFeign des autres services utilisent les endpoints `/patient/{patientId}/…` ci-dessus.
- **S8** : `praticienId` sera lu dans le token Keycloak ; création de partage réservée au patient et au dentiste (403).
