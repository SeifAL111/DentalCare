# 🦷 DentalCare

Plateforme d'orchestration des parcours de soins dentaires, basée sur une **architecture microservices** sécurisée et enrichie par des **agents IA (Spring AI, Ollama, pgvector)**.

---

## 👥 Équipe du Projet
* Nour El Houda Trabelsi
* Arij Yousfi
* Seifeddine Alaimi
* Ahmed Bejaoui
* Ahmed Guesmi
* Youssef Ayachi

---

## 🏗️ Architecture et Découpage (7 Microservices)
Le projet se compose de 6 microservices métier indépendants (chacun disposant de sa propre base de données) et d'un service commun d'intelligence artificielle :

1. **Dossier Médical** (`H2`) : Gestion des antécédents, allergies et consentements.
2. **Plan de Traitement & Devis** (`H2`) : Pilote le parcours de soins et l'orchestration des Sagas.
3. **Agenda Clinique** (`H2`) : Gestion des praticiens, des fauteuils, des rendez-vous et de la liste d'attente.
4. **Odontogramme & Actes** (`MySQL`) : Historique immuable par dent (numérotation FDI).
5. **Laboratoire & Prothèses** (`PostgreSQL`) : Suivi du cycle de fabrication des prothèses et détection des retards.
6. **Suivi Post-opératoire** (`MySQL`) : Protocoles de suivi et remontée automatique des alertes patients.
7. **Coordination Intelligente** (`PostgreSQL + pgvector`) : Service commun hébergeant les 3 agents IA (Secrétaire, Clinique, Laboratoire) sous validation humaine.

**Infrastructure & Sécurité :**
* **API Gateway** (Spring Cloud Gateway)
* **Eureka Server** (Registre des services)
* **Config Server** (Configuration centralisée)
* **Keycloak** (Gestion des identités, rôles et tokens)
* **RabbitMQ** (Communication asynchrone et pattern Saga)

---

## 📂 Structure du Dépôt Git
Conformément aux consignes, le dépôt est structuré ainsi :

```text
dentalcare/
├── Backend/                 # Projets Spring Boot des microservices
├── Frontend/                # Application cliente
└── Documentation/           # Dossier de conception et supports
    ├── Presentation/        # Fichier PDF de la présentation (Séance 4)
    ├── Diagrams/            # Schéma d'architecture globale (.png)
    └── DentalCare_Dossier_Technique_Conception.pdf  # Dossier technique complet
```

---

## 🚀 Lancement du Projet (Prochainement)
Pour lancer l'ensemble de l'infrastructure et des services conteneurisés :

```bash
docker-compose up --build
```

> **Prérequis :** assurez-vous d'avoir Docker et Docker Compose installés sur votre machine.
