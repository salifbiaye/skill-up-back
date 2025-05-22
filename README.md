# Skill Up Backend

Backend application for Skill Up, a personal development and learning management system.

## Technologies Used

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT Authentication
- Maven

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL

## Setup

1. Clone the repository
2. Create a PostgreSQL database named `skillup`
3. Update the database configuration in `src/main/resources/application.yml` if needed
4. Set the JWT secret key in environment variable `JWT_SECRET` or update it in `application.yml`

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080 with the context path `/api`.

## API Endpoints

### Authentication

- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and get JWT token

### Notes

- GET `/api/notes` - Get all notes
- POST `/api/notes` - Create a new note
- PUT `/api/notes/{id}` - Update a note
- DELETE `/api/notes/{id}` - Delete a note

### Objectives

- GET `/api/objectives` - Get all objectives
- POST `/api/objectives` - Create a new objective
- PUT `/api/objectives/{id}` - Update an objective
- DELETE `/api/objectives/{id}` - Delete an objective
- PATCH `/api/objectives/{id}/progress` - Update objective progress
- PATCH `/api/objectives/{id}/status` - Update objective status

### Tasks

- GET `/api/tasks` - Get all tasks
- POST `/api/tasks` - Create a new task
- PUT `/api/tasks/{id}` - Update a task
- DELETE `/api/tasks/{id}` - Delete a task
- PATCH `/api/tasks/{id}/status` - Update task status

## Security

The application uses JWT (JSON Web Token) for authentication. To access protected endpoints:

1. Register or login to get a JWT token
2. Include the token in the Authorization header: `Bearer <token>`

## Development

### Project Structure

```
src/main/java/com/skillup/
├── controller/     # REST controllers
├── service/        # Business logic
├── repository/     # Data access
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── security/       # Security configuration
└── exception/      # Exception handling
``` 