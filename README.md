# Skill-Up Backend

![Skill-Up Logo](https://img.shields.io/badge/Skill--Up-Backend-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-green)

Backend de l'application Skill-Up, une plateforme de développement personnel permettant de gérer objectifs, tâches et notes d'apprentissage.

## ✨ Fonctionnalités

- **Authentification** avec JWT
- **Gestion des objectifs** et suivi de progression
- **Gestion des tâches** liées aux objectifs
- **Prise de notes** d'apprentissage
- **Système d'achievements** pour motiver les utilisateurs
- **Assistant IA** via chat pour l'aide et les conseils

## 🏗 Architecture

Application Spring Boot organisée en modules fonctionnels :

```
com.skillup/
├── auth/           # Authentification
├── goals/          # Objectifs
├── tasks/          # Tâches
├── notes/          # Notes
├── achievements/   # Achievements
├── profile/        # Profil utilisateur
├── chat/           # Assistant IA
```

## 🛠 Technologies

- Java 17
- Spring Boot 3.2.3
- Spring Security avec JWT
- Spring Data JPA
- MySQL
- Maven
- Docker

## 📚 API Principales

- **Auth** : `/api/auth/register`, `/api/auth/login`
- **Objectifs** : `/api/objectives`
- **Tâches** : `/api/tasks`
- **Notes** : `/api/notes`
- **Achievements** : `/api/achievements`
- **Profil** : `/api/profile`
- **Chat** : `/api/chat-sessions`

## 📖 Documentation

Consultez la [documentation complète](docs/index.md) pour plus d'informations sur :
- [Rapport Backend](docs/rapport_backend.md)
- [Configuration DevOps](docs/rapport_devops.md) par Salif Biaye
- [Services détaillés](docs/services/)

## 👥 Équipe

Projet réalisé par :
- **Cheikh Ahmed Tidiane Thiandoum** - Développeur Backend
- **Awaa Ndiaye** - Développeuse Backend
- **Salif Biaye** - DevOps
