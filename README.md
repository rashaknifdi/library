# Library API - Examinerande individuell uppgift.

Ett REST‑API för att hantera böcker, författare och lån.  
Projektet inkluderar:

- Versionering (v1 & v2)
- DTO‑lager
- Global felhantering
- Integrationstester
- Optimistic locking
- Säkerhet (Spring Security, CORS, input validation)
- Prestandaoptimering (Redis caching, pagination, rate limiting)
- Benchmarking av caching (Mål 8)
- Vault‑förberedelse för hemlighetshantering (Mål 9)

---

## Getting Started

### Köra projektet

```bash
./mvnw spring-boot:run
```

API körs på:

```
http://localhost:8080
```

---

## API‑design

### Books – v1

- POST `/api/v1/books` – Skapa bok
- GET `/api/v1/books` – Hämta alla böcker
- GET `/api/v1/books/{id}` – Hämta bok efter ID

### Books – v2

Returnerar wrapper‑objekt:

```json
{
  "version": "v2",
  "data": [ ... ]
}
```

- GET `/api/v2/books` – Hämta böcker med versionering

### Authors

- POST `/api/v1/authors` – Skapa author
- GET `/api/v1/authors/{id}` – Hämta author
- GET `/api/v1/authors/{id}/books` – Hämta alla böcker för en author

### Loans

- POST `/api/v1/loans` – Skapa lån
- GET `/api/v1/loans` – Hämta alla lån

---

## DTO‑lager

### BookRequest

```java
public record BookRequest(
    String title,
    String isbn,
    int publishedYear,
    Long authorId
) {}
```

### BookResponse

```java
public record BookResponse(
    Long id,
    String title,
    String isbn,
    int publishedYear,
    AuthorResponse author
) {}
```

---

## Integrationstester

Projektet innehåller integrationstester som verifierar:

- Skapa author och book
- Skapa loan
- Låna redan utlånad bok (400)
- Hämta saknad resurs (404)
- Databas rensas mellan tester
- Testerna är oberoende

### Köra tester

```bash
./mvnw test
```

---

## Concurrency Test

Systemet testas med 100 parallella requests som försöker låna samma bok.

### Optimistic locking

```java
@Version
private Long version;
```

### Transaktionell lånelogik

```java
@Transactional
```

### Resultat

- Endast ett lån skapas
- Övriga requests får korrekt 400‑fel
- Testet är grönt i både IntelliJ och Maven

---

## Skalbarhetsreflektion

När flera användare försöker låna samma bok samtidigt uppstår race conditions.
Optimistic locking säkerställer att endast första transaktionen lyckas och att övriga får ett tydligt felmeddelande.
Systemet blir trådsäkert och stabilt även vid hög belastning.

---

## HTTPie Examples

### Skapa Author

```bash
http -a admin:password POST :8080/api/v1/authors name="Naguib Mahfouz"
```

### Skapa Book

```bash
http -a admin:password POST :8080/api/v1/books title="Palace Walk" isbn="1234" publishedYear:=1956 authorId:=1
```

### Skapa Loan

```bash
http -a admin:password POST :8080/api/v1/loans bookId:=1 borrower="Rasha"
```

### Låna redan utlånad bok

```bash
http -a admin:password POST :8080/api/v1/loans bookId:=1 borrower="Another User"
```

### Skapa Book utan titel

```bash
http -a admin:password POST :8080/api/v1/books  isbn="456" publishedYear:=1989 authorId:=1
```
### Skapa Book utan author

```bash
http -a admin:password POST :8080/api/v1/books  title="Romeo and Juliet" isbn="789" publishedYear:=2009 
```

### hämta Book som inte finns

```bash
http -a admin:password GET :8080/api/v1/books/999 
```

### Skapa Loan men boken saknas

```bash
http -a admin:password POST :8080/api/v1/loans bookId:=999 borrower="Rasha"
```

---
## Säkerhet

### Spring Security

Alla endpoints under /api/** är skyddade med Basic Authentication.
Klienten måste skicka användarnamn och lösenord i varje request.

Standardanvändaren:

username: admin

password: password

Om authentication saknas eller är felaktig returnerar API:t 401 Unauthorized.

Exempel:

**Korrekt anrop (med authentication)**

```bash

http -a admin:password GET :8080/api/v1/books

```
Detta anrop lyckas och API:t svarar med JSON‑data.

**Felaktigt anrop (utan authentication)**

```bash

http GET :8080/api/v1/books

```
Svar:
HTTP/1.1 401 Unauthorized

**Felaktigt anrop (fel lösenord)**

```bash

http -a admin:fel GET :8080/api/v1/books

```
Svar:
HTTP/1.1 401 Unauthorized


### CORS‑policy
API:t använder en strikt CORS‑policy:

Endast specifika origins

Endast nödvändiga headers

Endast nödvändiga HTTP‑metoder

Credentials avstängt om det inte behövs
Exempel:

```java

 @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }

```

### Input Validation

Alla inkommande DTO:er valideras med Bean Validation, t.ex.:

@NotNull för obligatoriska fält

@NotBlank för strängar som inte får vara tomma

@Size för längdbegränsningar

@Min för numeriska minvärden

Vid valideringsfel returneras tydliga felmeddelanden via global felhantering.

### Spring Vault (förberett)

I utvecklingsläge ligger hemligheter i application.properties och Vault är avstängt:
spring.cloud.vault.enabled=false

Vid deployment är lösningen förberedd för att flytta:

Databaslösenord

Eventuella API‑nycklar
till Spring Vault, så att de inte ligger i källkod eller konfigurationsfiler.

---
## Optimering (Mål 8)
### Redis Caching
Redis‑cache är aktiverad för tunga läsoperationer, t.ex.:

GET /api/v1/books/{id}

Implementerat med @Cacheable("books") i service‑lagret.

```java
@Cacheable(value = "books", key = "#id")
public BookResponse getBookById(Long id) { ... }
```

Första anropet hämtar från databasen, efterföljande anrop går mot cache.
### Pagination
Alla list‑endpoints (t.ex. GET /api/v1/books) använder Pageable:

GET /api/v1/books?page=0&size=20

Förhindrar att för stora datamängder skickas i ett svar.

Ger bättre prestanda och skalbarhet.

### Rate Limiting
Rate limiting är implementerat med Bucket4j.

Exempel: 10 anrop per minut per IP‑adress.
Vid överskridande returneras HTTP 429 (Too Many Requests).

```java
Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))); 
```

---

## Prestandamätning (Mål 8) – Benchmarking

Syftet är att mäta responstider före och efter Redis‑caching.

**Endpoint:**
```text
GET /api/v1/books/{id}
```

### Mätning utan caching
Anrop går direkt mot databasen.

| Anrop | Tid (ms) |
|-------|----------|
| 1     | 359      |
| 2     | 156      |
| 3     | 140      |
| 4     | 149      |
| 5     | 131      |
| 6     | 163      |
| 7     | 130      |
| 8     | 182      |
| 9     | 151      |
| 10    | 126      |

**Snitt:** 169 ms

### Mätning med Redis‑caching
Första anropet är en cache miss (data hämtas från DB och skrivs till Redis).

| Anrop    | Tid (ms) |
|----------|----------|
| 1 (miss) | 274      |
| 2        | 130      |
| 3        | 137      |
| 4        | 148      |
| 5        | 157      |
| 6        | 156      |
| 7        | 129      |
| 8        | 130      |
| 9        | 137      |
| 10       | 126      |

**Snitt:** 139 ms

### 📊 Resultat
$$\frac{169 - 139}{169} \times 100 \approx 17.75\%$$

**Slutsats:** $\approx$ 18% snabbare med Redis‑caching i denna miljö.

### 📝 Analys
Skillnaden är relativt liten i detta specifika testscenario eftersom:
1. **Enkel fråga**: Endpointen hämtar en enda bok via primärnyckel, vilket är en extremt snabb SQL-fråga.
2. **Lokal miljö**: Både Redis och H2-databasen körs lokalt, vilket eliminerar nätverkslatens.
3. **Overhead**: Spring Security, JSON-serialisering och nätverksstacken står för den största delen av den totala tiden per anrop.

I större system med tunga JOIN-frågor, stora listor eller distribuerade databaser ger Redis ofta en förbättring på **90–99%**.

---

## Definition of Done

| Krav | Status |
|------|--------|
| API v1 och v2 fungerar | Klar |
| DTO används konsekvent | Klar |
| Global felhantering | Klar |
| Author + Loan fungerar | Klar |
| Affärsregler upprätthålls | Klar |
| Integrationstester gröna | Klar |
| Concurrency‑test grönt | Klar |
| Race conditions lösta | Klar |
| Skalbarhetsreflektion klar | Klar |
| Spring Security & CORS | Klar |
| Input Validation | Klar |
| Spring Vault (förberett) | Klar |
| Redis Caching | Klar |
| Pagination (Pageable) | Klar |
| Rate Limiting (Bucket4j) | Klar |
| Benchmarking Rapport | Klar |


---

## Utvecklare

Namn: Rasha Knifdi  
Kurs:  Backend API – Chas Academy
