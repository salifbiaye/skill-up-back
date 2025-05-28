# Service d'Authentification

*Développé par : Cheikh Ahmed Tidiane Thiandoum & Awaa Ndiaye*

## Vue d'ensemble

Le service d'authentification est la porte d'entrée de l'application Skill-Up. Il gère l'inscription des utilisateurs, leur connexion, et la sécurisation des endpoints de l'API via JWT (JSON Web Tokens).

## Architecture

```mermaid
classDiagram
    class AuthController {
        +register(RegisterRequest) AuthResponse
        +login(LoginRequest) AuthResponse
        +refreshToken() AuthResponse
    }
    
    class AuthService {
        -userRepository UserRepository
        -passwordEncoder PasswordEncoder
        -jwtService JwtService
        -achievementService AchievementService
        +register(RegisterRequest) AuthResponse
        +authenticate(LoginRequest) AuthResponse
        +refreshToken(String) AuthResponse
    }
    
    class JwtService {
        -secretKey String
        -tokenValidity Long
        +generateToken(UserDetails) String
        +validateToken(String, UserDetails) boolean
        +extractUsername(String) String
    }
    
    class User {
        -id Long
        -username String
        -email String
        -password String
        -role Role
        -createdAt LocalDateTime
        -lastLogin LocalDateTime
    }
    
    class UserRepository {
        +findByEmail(String) Optional~User~
        +findByUsername(String) Optional~User~
        +existsByEmail(String) boolean
        +existsByUsername(String) boolean
    }
    
    AuthController --> AuthService
    AuthService --> UserRepository
    AuthService --> JwtService
    AuthService --> AchievementService
    UserRepository --> User
```

## Flux d'Authentification

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant UserRepository
    participant JwtService
    participant AchievementService
    
    %% Inscription
    Client->>AuthController: POST /auth/register
    AuthController->>AuthService: register(RegisterRequest)
    AuthService->>UserRepository: existsByEmail(email)
    UserRepository-->>AuthService: boolean
    AuthService->>UserRepository: existsByUsername(username)
    UserRepository-->>AuthService: boolean
    
    alt Email ou Username déjà utilisé
        AuthService-->>AuthController: Exception
        AuthController-->>Client: 400 Bad Request
    else Données valides
        AuthService->>UserRepository: save(User)
        UserRepository-->>AuthService: User
        AuthService->>AchievementService: initializeAchievements(User)
        AuthService->>JwtService: generateToken(User)
        JwtService-->>AuthService: token
        AuthService-->>AuthController: AuthResponse
        AuthController-->>Client: 200 OK + token + user info
    end
    
    %% Connexion
    Client->>AuthController: POST /auth/login
    AuthController->>AuthService: authenticate(LoginRequest)
    AuthService->>UserRepository: findByEmail(email)
    UserRepository-->>AuthService: Optional<User>
    
    alt User non trouvé
        AuthService-->>AuthController: Exception
        AuthController-->>Client: 401 Unauthorized
    else User trouvé
        AuthService->>PasswordEncoder: matches(rawPassword, encodedPassword)
        
        alt Mot de passe incorrect
            AuthService-->>AuthController: Exception
            AuthController-->>Client: 401 Unauthorized
        else Mot de passe correct
            AuthService->>UserRepository: updateLastLogin(User)
            AuthService->>JwtService: generateToken(User)
            JwtService-->>AuthService: token
            AuthService-->>AuthController: AuthResponse
            AuthController-->>Client: 200 OK + token + user info
        end
    end
```

## Points Clés d'Implémentation

### Sécurité des Mots de Passe

Les mots de passe sont encodés avec BCrypt avant d'être stockés en base de données :

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Génération de JWT

Les tokens JWT contiennent les informations essentielles de l'utilisateur et ont une durée de validité configurable :

```java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    if (userDetails instanceof User) {
        User user = (User) userDetails;
        claims.put("id", user.getId());
        claims.put("role", user.getRole());
    }
    
    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}
```

### Validation des Tokens

Chaque requête à un endpoint protégé passe par un filtre qui valide le token JWT :

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // ...
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        final String username = jwtService.extractUsername(jwt);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtService.validateToken(jwt, userDetails)) {
                // Configure l'authentification dans le contexte de sécurité
                // ...
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Intégration avec le Système d'Achievements

Lors de l'inscription, les achievements de l'utilisateur sont initialisés :

```java
@Override
public AuthResponse register(RegisterRequest request) {
    // Validation et création de l'utilisateur
    // ...
    
    User savedUser = userRepository.save(user);
    
    // Initialisation des achievements
    achievementService.initializeAchievements(savedUser);
    
    // Génération du token
    // ...
}
```

## Endpoints API

| Méthode | Endpoint | Description | Paramètres | Réponse |
|---------|----------|-------------|------------|---------|
| POST | `/api/auth/register` | Inscription d'un nouvel utilisateur | `username`, `email`, `password` | JWT token + infos utilisateur |
| POST | `/api/auth/login` | Connexion d'un utilisateur | `email`, `password` | JWT token + infos utilisateur |
| POST | `/api/auth/refresh` | Rafraîchissement du token | Token dans l'en-tête Authorization | Nouveau JWT token |

## Défis et Solutions

### Défi : Sécurité des Tokens

**Solution :** Utilisation d'une clé secrète robuste stockée dans les variables d'environnement et rotation régulière des tokens.

### Défi : Gestion des Sessions

**Solution :** Implémentation d'une approche stateless avec JWT, tout en maintenant un suivi des connexions pour l'achievement "Apprentissage constant".

## Améliorations Futures

1. Authentification à deux facteurs
2. Support de l'authentification OAuth2 (Google, GitHub, etc.)
3. Système de récupération de mot de passe
4. Blacklisting des tokens révoqués
