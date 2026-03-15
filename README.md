# P5 Full Stack — README

## Aperçu

Ce projet est une application **full stack** composée de :

- un **frontend Angular 19**
- un **backend Spring Boot 3.5.5**
- une base de données **MySQL**

---

## Installation du projet

### 1) Cloner le projet

`git clone <url-du-repo>`

---

## Frontend

### Stack technique

- Angular **19.2.x**
- Node.js **22.18.0**
- Jest pour les tests unitaires
- Cypress pour les tests e2e
- LightHouse pour la qualité

### Installation

Depuis `front/` :

`npm install`

### Lancer le frontend

Depuis `front/` :

`ng serve`

Le frontend sera disponible sur :

- `http://localhost:4200`

### Commandes utiles

Depuis `front/` :

#### Build de développement

`npm run build`

#### Build de production

`npm run build:prod`

#### Tests unitaires

`npm run test`

#### Tests e2e

`npm run e2e`

#### Couverture e2e

`npm run e2e:coverage`

Le dossier de couverture est généré ici :

`front/coverage/`

#### Audit LightHouse

`npm run lighthouse`

---

## Backend

### Stack technique

- Spring Boot **3.5.5**
- Java **21**
- Maven
- Spring Security
- Spring Data JPA
- MySQL
- JaCoCo pour la couverture de tests
- Sonar Maven Plugin
- SpringDoc OpenAPI / Swagger UI

### Configuration de l'environnement

Le backend lit ses secrets et identifiants depuis les variables d'environnement.

Le code backend attend **ces noms exacts** :

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Pour l'analyse Sonar, il est pratique d'ajouter aussi :

- `SONAR_TOKEN`

### Base de données

L'application utilise **MySQL** sur le port :

- `3306`

### Script SQL

Le script SQL est présent ici :

`Ressources/script.sql`

### Lancer le backend

Depuis `back/` :

`mvn spring-boot:run`

Le backend écoute sur :

- `http://localhost:3001`

### Commandes utiles

Depuis `back/` :

#### Exécuter les tests et générer le rapport de couverture

`mvn clean test`

Rapport généré dans :

`back/target/site/jacoco/index.html`

#### Vérifier les seuils de couverture

`mvn clean verify`

> Le projet applique un seuil de couverture minimum de **70 %** sur plusieurs métriques JaCoCo.

#### Analyse Sonar

`mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN`

### Documentation API

Une fois l'API démarrée, la documentation est accessible ici :

- `http://localhost:3001/swagger-ui/index.html`

---

## Conseils de démarrage

Ordre conseillé :

1. Importer le script SQL dans MySQL
2. Vérifier que MySQL tourne sur le port `3306`
3. Définir les variables d'environnement du backend
4. Lancer le backend
5. Lancer le frontend
6. Ouvrir `http://localhost:4200`