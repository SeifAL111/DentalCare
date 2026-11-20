# Microservice Suivi Post-opératoire (DentalCare - Microservice n°6)

**Auteur :** Youssef Ayachi  
**Rôle :** Microservice n°6 — Suivi Post-opératoire  
**Projet :** DentalCare (Architecture Microservices)

---

## 1. Description du Service

Ce microservice a pour mission d'assurer la surveillance médicale du patient après un acte dentaire :
- **Protocoles post-opératoires** : Paramétrage des consignes médicales, des jours de contrôle (J+1, J+3, etc.) et des questions associées.
- **Suivi patient** : Ouverture du dossier de suivi dès la réalisation de l'acte dentaire.
- **Réponses & Détection d'alertes** : Évaluation des notes fournies par le patient (0 à 10). Si la valeur dépasse le seuil critique (ex: seuil 7), une **Alerte** est créée automatiquement et le suivi passe au statut `EN_ALERTE`.
- **Workflow clinique** : Prise en charge par l'équipe soignante (`PRIS_EN_CHARGE`) et clôture par le praticien (`CLOTURE`).

---

## 2. Stack Technique

- **Java :** 17
- **Framework :** Spring Boot 3.x / 4.x
- **Build tool :** Maven Wrapper (`mvnw.cmd` sous Windows)
- **Base de données :** MySQL 8 (base `suivi_postoperatoire`)
- **Port du service :** `8086`
- **Nom du service :** `suivi-postoperatoire`
- **Service Discovery :** Netflix Eureka Client (`http://localhost:8761/eureka`)
- **Documentation API :** Swagger UI via SpringDoc (`http://localhost:8086/swagger-ui.html`)

---

## 3. Configuration & Démarrage

### A. Prérequis
1. **MySQL (XAMPP ou Standalone)** démarré sur le port `3307` (ou `3306`).
   - La base `suivi_postoperatoire` sera créée automatiquement au premier lancement si elle n'existe pas grâce à `createDatabaseIfNotExist=true`.
2. **Eureka Server** (optionnel pour tester en local direct, mais nécessaire pour la découverte globale).

### B. Variables d'environnement configurables (`application.yml`)
| Variable | Description | Valeur par défaut |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL JDBC complète de connexion MySQL | `jdbc:mysql://127.0.0.1:3307/suivi_postoperatoire...` |
| `DB_USER` | Nom d'utilisateur MySQL | `root` |
| `DB_PASSWORD` | Mot de passe MySQL | *(vide)* |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL d'enregistrement Eureka | `http://127.0.0.1:8761/eureka/` |

> [!NOTE]
> Si votre MySQL tourne sur le port standard **3306**, lancez la commande en passant la variable :
> ```powershell
> $env:SPRING_DATASOURCE_URL="jdbc:mysql://127.0.0.1:3306/suivi_postoperatoire?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
> .\mvnw.cmd spring-boot:run
> ```

### C. Procédure de lancement pas à pas

1. **Étape 1 : Lancer Eureka Server (Terminal 1)**
   ```powershell
   cd c:\Users\ADMIN\Downloads\BackEndRestFul_SpringBoot\DentalCare\Backend\eureka-server
   .\mvnw.cmd spring-boot:run
   ```
   *Vérifier sur le navigateur : `http://localhost:8761`.*

2. **Étape 2 : Lancer le microservice Suivi Post-opératoire (Terminal 2)**
   ```powershell
   cd c:\Users\ADMIN\Downloads\BackEndRestFul_SpringBoot\DentalCare\Backend\suivi-postoperatoire
   .\mvnw.cmd spring-boot:run
   ```
   *Le service démarre sur le port **8086** et s'enregistre auprès d'Eureka.*

3. **Étape 3 : Accéder à l'interface Swagger UI**
   Ouvrez votre navigateur sur :  
   👉 **http://localhost:8086/swagger-ui.html**

---

## 4. Données de Démonstration automatiques

Au démarrage (`DataInitializer`), si la base est vide, le système insère automatiquement :
- **Protocole :** `DEVITALISATION` (ID: 1)
- **Jours de suivi :** `[1, 3]`
- **Question :** *"Quelle est l'intensité de votre douleur de 0 à 10 ?"* (ID: 1, seuil d'alerte : **7**)
- **Consignes :** Conseils réalistes pour les antalgiques, l'alimentation et le brossage.

---

## 5. Scénario de Test Swagger : Le Parcours d'Ahmed

Voici la séquence exacte à dérouler dans l'interface Swagger (`http://localhost:8086/swagger-ui.html`) pour valider l'ensemble du microservice et des règles métier.

---

### 🧪 Étape 1 : Créer un nouveau protocole (ou vérifier celui existant)
* **Endpoint :** `POST /api/protocoles`
* **Objectif :** Créer un protocole d'extraction de dent de sagesse.
* **Body JSON à coller :**
```json
{
  "acteCode": "EXTRACTION_SAGESSE",
  "jours": [1, 2, 4],
  "consignes": "Appliquer de la glace 15 min par heure le premier jour. Ne pas cracher ni rincer la bouche pendant 24h. Privilégier une alimentation tiède ou froide et molle.",
  "questions": [
    {
      "libelle": "Évaluez votre douleur actuelle de 0 à 10",
      "seuilAlerte": 6
    }
  ]
}
```
* **Résultat attendu :** `HTTP 201 Created` avec l'ID du protocole généré (ex: ID 2).

---

### 🧪 Étape 2 : Ouvrir un suivi pour le patient Ahmed
* **Endpoint :** `POST /api/suivis`
* **Objectif :** Déclencher le suivi d'Ahmed suite à sa dévitalisation (Protocole ID 1).
* **Body JSON à coller :**
```json
{
  "patientId": "PATIENT-AHMED",
  "acteId": 101,
  "protocoleId": 1
}
```
* **Résultat attendu :** `HTTP 201 Created`
```json
{
  "id": 1,
  "patientId": "PATIENT-AHMED",
  "acteId": 101,
  "renforce": false,
  "statut": "ACTIF",
  "protocoleId": 1,
  "acteCode": "DEVITALISATION",
  "dateOuverture": "..."
}
```

---

### 🧪 Étape 3 : Consulter les consignes post-opératoires d'Ahmed
* **Endpoint :** `GET /api/suivis/1/consignes`
* **Résultat attendu :** `HTTP 200 OK` avec le texte des consignes de dévitalisation.

---

### 🧪 Étape 4 : Le patient répond à J+1 avec une douleur modérée (Valeur = 3)
* **Endpoint :** `POST /api/suivis/1/reponses`
* **Règle métier testée :** $3 < 7$ (seuil) ➔ **Pas d'alerte**, le statut reste `ACTIF`.
* **Body JSON à coller :**
```json
{
  "jour": 1,
  "questionId": 1,
  "valeur": 3
}
```
* **Résultat attendu :** `HTTP 201 Created`
```json
{
  "id": 1,
  "jour": 1,
  "valeur": 3,
  "alerteCreee": false,
  "motifAlerte": null,
  "nouveauStatutSuivi": "ACTIF"
}
```

---

### 🧪 Étape 5 : Le patient répond à J+3 avec une douleur aiguë (Valeur = 8)
* **Endpoint :** `POST /api/suivis/1/reponses`
* **Règle métier testée :** $8 \ge 7$ (seuil) ➔ **Alerte générée** + passage automatique au statut `EN_ALERTE`.
* **Body JSON à coller :**
```json
{
  "jour": 3,
  "questionId": 1,
  "valeur": 8
}
```
* **Résultat attendu :** `HTTP 201 Created`
```json
{
  "id": 2,
  "jour": 3,
  "valeur": 8,
  "alerteCreee": true,
  "motifAlerte": "Douleur 8 à J+3 (seuil 7)",
  "nouveauStatutSuivi": "EN_ALERTE"
}
```

---

### 🧪 Étape 6 : Consultation du tableau de bord des alertes
* **Endpoint :** `GET /api/suivis/alertes`
* **Résultat attendu :** `HTTP 200 OK`. Le suivi d'Ahmed apparaît dans la liste des cas nécessitant une intervention soignante.

---

### 🧪 Étape 7 : Prise en charge par l'équipe soignante
* **Endpoint :** `PATCH /api/suivis/1/prendre-en-charge`
* **Règle métier testée :** Transition `EN_ALERTE` ➔ `PRIS_EN_CHARGE`.
* **Résultat attendu :** `HTTP 200 OK` avec `"statut": "PRIS_EN_CHARGE"`.

---

### 🧪 Étape 8 : Clôture définitive du suivi
* **Endpoint :** `PATCH /api/suivis/1/cloturer`
* **Règle métier testée :** Passage à `CLOTURE`.
* **Résultat attendu :** `HTTP 200 OK` avec `"statut": "CLOTURE"`.

---

### 🧪 Étape 9 : Suppression physique (CRUD DELETE)
* **Endpoint :** `DELETE /api/suivis/1`
* **Objectif :** Supprimer un suivi (en cas d'erreur de saisie ou droit à l'effacement). Cascade automatique sur ses réponses et alertes.
* **Résultat attendu :** `HTTP 204 No Content`.

---

### 🧪 Étape 10 : Tests des cas d'erreurs (Validation des règles de robustesse)

1. **Cas d'erreur 409 : Doublon de réponse**
   - Renvoyer la même réponse pour J+1 (`jour: 1, questionId: 1, valeur: 3`).
   - **Résultat :** `HTTP 409 CONFLICT` avec le message *"Une réponse a déjà été enregistrée pour ce suivi au jour J+1 pour la question n°1"*.

2. **Cas d'erreur 409 : Réponse sur suivi clôturé**
   - Tenter d'envoyer une nouvelle réponse sur le suivi n°1 maintenant qu'il est clôturé.
   - **Résultat :** `HTTP 409 CONFLICT` avec le message *"Impossible d'enregistrer une réponse : le suivi est clôturé."*.

3. **Cas d'erreur 400 : Jour hors protocole**
   - Essayer d'envoyer une réponse au jour 2 alors que le protocole ne prévoit que les jours `[1, 3]`.
   - **Résultat :** `HTTP 400 BAD REQUEST` avec le message *"Le jour 2 n'appartient pas à la liste des jours prévus par le protocole : [1, 3]"*.

4. **Cas d'erreur 400 : Note hors échelle (0 à 10)**
   - Envoyer `"valeur": 15`.
   - **Résultat :** `HTTP 400 BAD REQUEST` avec le message *"La valeur maximale est 10"*.

5. **Cas d'erreur 404 : Ressource inexistante**
   - Exécuter `GET /api/suivis/999/consignes`.
   - **Résultat :** `HTTP 404 NOT FOUND` avec le message *"Suivi introuvable avec l'identifiant : 999"*.

---

## 6. Guide des Erreurs Classiques et Solutions

| Erreur rencontrée | Cause probable | Solution rapide |
| :--- | :--- | :--- |
| `Communications link failure` / `Connection refused` | MySQL n'est pas démarré ou n'écoute pas sur le port configuré. | Vérifier dans XAMPP que MySQL est vert (port 3307 ou 3306). Si port 3306, passer `$env:SPRING_DATASOURCE_URL="jdbc:mysql://127.0.0.1:3306/suivi_postoperatoire..."`. |
| `Cannot execute request on any known server` (Eureka) | Le serveur Eureka n'a pas encore été démarré sur le port 8761. | Démarrer `eureka-server` dans un terminal séparé. Le microservice continue de fonctionner en local même si Eureka démarre après. |
| `ConflictException (409)` lors de la prise en charge | Le suivi n'était pas au statut `EN_ALERTE`. | La prise en charge est strictement réservée aux suivis dont le statut actuel est `EN_ALERTE`. |
| `ConflictException (409)` à la création de protocole | Le code d'acte (`acteCode`) existe déjà en base de données. | Les codes d'actes sont uniques (ex: `DEVITALISATION`, `EXTRACTION_SAGESSE`). Utiliser un code différent ou réutiliser le protocole existant. |
| `Whitelabel Error Page` sur Swagger | Mauvaise URL ou chemin non configuré. | Utiliser impérativement l'URL complète : `http://localhost:8086/swagger-ui.html`. |
