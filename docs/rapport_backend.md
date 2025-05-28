# Rapport Backend - Skill-Up

*Réalisé par : Cheikh Ahmed Tidiane Thiandoum & Awaa Ndiaye*

## Présentation du projet

Skill-Up est une application de développement personnel permettant aux utilisateurs de gérer leurs objectifs, tâches et notes d'apprentissage. Le backend a été développé avec Spring Boot et fournit une API REST pour l'application frontend.

## Architecture

Le backend est organisé en modules fonctionnels :

```
com.skillup/
├── auth/           # Authentification
├── goals/          # Gestion des objectifs
├── tasks/          # Gestion des tâches
├── notes/          # Système de notes
├── achievements/   # Système de récompenses
├── profile/        # Profils utilisateurs
├── chat/           # Assistant IA
```

## Technologies utilisées

- Java 17
- Spring Boot 3.2.3
- Spring Security avec JWT
- Spring Data JPA
- MySQL
- Maven

## Fonctionnalités principales

### Authentification
- Inscription et connexion des utilisateurs
- Sécurisation des endpoints avec JWT

### Gestion des objectifs et tâches
- Création et suivi d'objectifs personnels
- Décomposition en tâches avec statuts
- Calcul automatique de progression

### Prise de notes
- Création et organisation de notes d'apprentissage
- Recherche par mots-clés

### Système d'achievements
- Récompenses pour les actions des utilisateurs :
  - Premier pas (inscription)
  - Objectifs fixés (création d'objectif)
  - Tâches accomplies (5 tâches terminées)
  - Prise de notes (10 notes créées)
  - Apprentissage constant (connexions régulières)

### Assistant IA
- Chat avec intelligence artificielle via OpenRouter
- Aide contextuelle pour les objectifs et l'apprentissage

## Développement par sprints

### Sprint 1 : Fondation et Authentification
- Configuration du projet Spring Boot
- Mise en place de la base de données
- Développement de l'authentification avec JWT

### Sprint 2 : Objectifs et Tâches
- Création des entités et services pour les objectifs
- Création des entités et services pour les tâches
- Mise en place du suivi de progression

### Sprint 3 : Notes et Achievements
- Développement du système de notes
- Mise en place du système d'achievements
- Tests et corrections

## Documentation des services

Pour plus de détails sur chaque service, consultez les documents dans le dossier [services](services/) :

- [Service d'Authentification](services/service_authentification.md)
- [Service de Gestion des Objectifs](services/service_objectifs.md)
- [Service de Gestion des Tâches](services/service_taches.md)
- [Service de Gestion des Notes](services/service_notes.md)
- [Service d'Achievements](services/service_achievements.md)
- [Service de Profil Utilisateur](services/service_profil.md)
- [Service de Chat avec IA](services/service_chat.md)

## Déploiement

Le déploiement a été configuré par Salif Biaye avec Docker et Docker Compose. Pour plus d'informations, consultez le [Rapport DevOps](rapport_devops.md).
