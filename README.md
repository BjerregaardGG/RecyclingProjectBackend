# Snatch — Backend

Backend til **Snatch**, en mobilapp hvor brugere kan give brugte genstande væk til folk i nærområdet i stedet for at smide dem ud. Bygget som hovedopgave på datamatikeruddannelsen.

Dette repository indeholder backend-API'et. Frontend findes i et separat repo.

## Indhold

- [Funktionalitet](#funktionalitet)
- [Teknologier](#teknologier)
- [Arkitektur](#arkitektur)
- [Kom i gang](#kom-i-gang)
- [API-endpoints](#api-endpoints)
- [Database](#database)
- [Sikkerhed](#sikkerhed)
- [Tests](#tests)
- [Projektstruktur](#projektstruktur)

## Funktionalitet

Backend understøtter alle appens kernefunktioner:

- **Brugerhåndtering** — registrering, login, profilopdatering, glemt adgangskode
- **Items** — oprettelse af opslag med billede, beskrivelse, lokation og kategori
- **Afhentning (Pickup)** — anmodninger, accept/afvisning, gensidig bekræftelse af afhentning
- **Chat** — beskeder mellem giver og modtager via WebSocket
- **Likes** — brugere kan markere items som favoritter
- **Anmeldelser** — gensidig review-funktion efter en afhentning
- **Notifikationer** — beskeder om nye anmodninger, beskeder, accept osv.

## Teknologier

| Område | Valg |
|---|---|
| Sprog | Java 21 |
| Framework | Spring Boot 3 |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Sikkerhed | Spring Security + JWT |
| WebSocket | Spring WebSocket + STOMP |
| Email | Spring Mail (SMTP) |
| Billede-upload | Cloudinary (via frontend) |
| Build | Maven |
| Tests | JUnit 5 + Mockito |
| CI | GitHub Actions |

## Arkitektur

Projektet følger en klassisk lagdelt arkitektur:

```
Controller  →  Service  →  Repository  →  Database
                  ↓
              DTO Mapper  →  DTO  (returneres til klient)
```

**Lag og ansvar:**

- **Controller** — modtager HTTP-requests, validerer input, kalder service
- **Service** — indeholder forretningslogik (validering, regler, transaktioner)
- **Repository** — kommunikerer med databasen via Spring Data JPA
- **DTO Mapper** — oversætter mellem entiteter og DTO'er, så vi ikke eksponerer database-modellen direkte

Hvert domæneområde (user, item, pickup, message osv.) har sin egen pakke med disse lag.

**Fejlhåndtering** sker centralt via en `@RestControllerAdvice` der fanger `ResponseStatusException` og returnerer JSON med status, besked og tidsstempel.

**Authentifikation** sker via JWT-tokens. Et custom `JwtFilter` validerer tokens på hver request og sætter den autentificerede bruger i `SecurityContextHolder`.

## Kom i gang

### Forudsætninger

- Java 21
- Maven 3.9+
- MySQL 8+
- En Cloudinary-konto (til billeder)
- En SMTP-server (fx Gmail med app-password) til mail

### Setup

1. **Klon repoet**
   ```bash
   git clone <https://github.com/BjerregaardGG/RecyclingProjectBackend>
   cd snatch-backend
   ```

2. **Opret database**
   ```sql
   CREATE DATABASE snatch;
   ```

3. **Konfigurér `application.properties`**

   Opret `src/main/resources/application.properties` (eller brug environment variables):

   ```properties
    # JWT
    jwt.secret=<en-lang-sikker-streng>

    # Database
    spring.datasource.url=jdbc:mysql://localhost:3306/snatch
    spring.datasource.username=<dit-username>
    spring.datasource.password=<dit-password>
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
    spring.jpa.hibernate.ddl-auto=update
    
    # Mail
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=<din-email>
    spring.mail.password=<app-password>
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    
    # Fejlhåndtering
    spring.web.error.include-message=always
    
    # Server
    server.address=0.0.0.0
    ```

4. **Kør applikationen**
   ```bash
   mvn spring-boot:run
   ```

   Backend kører nu på `http://localhost:8080`.

5. **Eksponér via ngrok** (hvis frontend kører på en fysisk telefon)
   ```bash
   ngrok http 8080
   ```

   Kopier ngrok-URL'en og brug den i frontendens `.env`-fil.

## API-endpoints

Et udsnit af de vigtigste endpoints. Alle endpoints undtagen `/api/auth/*` kræver JWT-token i `Authorization: Bearer <token>`-headeren.

### Auth

| Metode | Endpoint | Beskrivelse |
|---|---|---|
| POST | `/api/auth/register` | Opret bruger |
| POST | `/api/auth/login` | Login, returnerer JWT |
| POST | `/api/auth/forgot-password` | Send reset-mail |
| POST | `/api/auth/reset-password` | Sæt ny adgangskode med token |

### Users

| Metode | Endpoint | Beskrivelse |
|---|---|---|
| GET | `/api/users/me` | Hent indlogget bruger |
| GET | `/api/users/{id}` | Hent bruger |
| PATCH | `/api/users/me` | Opdater profil |

### Items

| Metode | Endpoint | Beskrivelse |
|---|---|---|
| GET | `/api/items` | Hent tilgængelige items |
| GET | `/api/items/{id}` | Hent et specifikt item |
| POST | `/api/items` | Opret nyt item |
| DELETE | `/api/items/{id}` | Slet eget item |

### Pickups

| Metode | Endpoint | Beskrivelse |
|---|---|---|
| GET | `/api/pickups/incoming` | Indgående anmodninger |
| GET | `/api/pickups/outgoing` | Udgående anmodninger |
| POST | `/api/pickups/items/{id}` | Anmod om at afhente |
| PATCH | `/api/pickups/{id}/accept` | Acceptér anmodning |
| PATCH | `/api/pickups/{id}/decline` | Afvis anmodning |
| PATCH | `/api/pickups/{id}/confirm` | Bekræft afhentning |

### Messages

| Metode | Endpoint | Beskrivelse |
|---|---|---|
| GET | `/api/messages/pickup/{id}` | Hent chathistorik |
| PATCH | `/api/messages/pickup/{id}/mark-as-read` | Marker beskeder som læst |
| WS | `/ws` (STOMP) | WebSocket-forbindelse til chat |

### Likes, Reviews, Notifications

Standard CRUD-endpoints — se controller-klasserne for detaljer.

## Database

Databasen oprettes automatisk af Hibernate ved opstart (`ddl-auto=update`). De vigtigste tabeller:

- **users** — brugere
- **items** — opslag
- **pickup_requests** — anmodninger med status (PENDING, ACCEPTED, REJECTED, COMPLETED, EXPIRED)
- **messages** — chatbeskeder knyttet til en pickup
- **reviews** — anmeldelser efter afhentning
- **item_likes** — like-relationer
- **notifications** — notifikationer

### Status-flow for pickup

```
PENDING ──acceptér──► ACCEPTED ──begge bekræfter──► COMPLETED
   │                     │
   │                     └──tid løber ud──► EXPIRED
   │
   └──afvis──► REJECTED
```

## Sikkerhed

- **Adgangskoder** hashes med BCrypt før de gemmes
- **JWT-tokens** signeres med HMAC-SHA-256 og indeholder bruger-ID samt udløbstid
- **Login** giver samme fejlbesked uanset om email eller adgangskode er forkert (forhindrer user enumeration)
- **`JwtFilter`** validerer token på hver request og sætter `SecurityContext`
- **CSRF** er disabled (vi bruger ikke cookies, kun bearer tokens)
- **Reset-tokens** til glemt-adgangskode er engangs-tokens med 1 times udløb

## Tests

Unit tests skrevet med JUnit 5 og Mockito. Hver service har sin egen testklasse.

Kør alle tests:

```bash
mvn test
```

Eksempler på test-strategier brugt i projektet:

- **ArgumentCaptor** til at verificere objekter der bygges inde i servicen
- **`verify(repo, never()).save(any())`** til at sikre at fejl-paths ikke gemmer data
- **`thenAnswer`** til at simulere database-id-tildeling ved save
- **`InOrder`** til at verificere rækkefølge af kald (fx slet → flush → save)

## CI/CD

GitHub Actions kører automatisk på hver push:

- `backend-tests.yml` kører `mvn test` på alle ændringer i backend-mappen
- Build-status vises i README-badget (kan tilføjes)

## Projektstruktur

```
src/main/java/com/recyclingprojectbackend/
├── auth/                     — login, register, JWT, mail
│   ├── controller/
│   ├── service/
│   ├── utility/              — JwtFilter, JwtUtility
│   └── dto/
├── user/
├── item/
├── item_like/
├── pickup_request/
├── message/
├── review/
├── like/
├── notification/
├── category/
├── config/
└── exception_handler/                — GlobalExceptionHandler
```

Hver mappe følger samme mønster: `controller/`, `service/`, `repository/`, `model/`, `dto/`.

## Forfatter

Oliver Bjerregaard

Lavet som hovedopgave på datamatikeruddannelsen, foråret 2026.
