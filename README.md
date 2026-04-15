# Library API - Examinerande individuell uppgift.

Ett REST‑API för att hantera böcker, författare och lån.
Projektet inkluderar versionering, DTO‑lager, global felhantering, integrationstester och concurrency‑skydd via optimistic locking.

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
http POST :8080/api/v1/authors name="Frank Herbert"
```

### Skapa Book

```bash
http POST :8080/api/v1/books title="Dune" isbn="123" publishedYear:=1965 authorId:=1
```

### Skapa Loan

```bash
http POST :8080/api/v1/loans bookId:=1 borrower="Rasha"
```

### Låna redan utlånad bok

```bash
http POST :8080/api/v1/loans bookId:=1 borrower="Another User"
```

### Skapa Book utan titel

```bash
http POST :8080/api/v1/books  isbn="456" publishedYear:=1989 authorId:=1
```
### Skapa Book utan author

```bash
http POST :8080/api/v1/books  title="Romeo and Juliet" isbn="789" publishedYear:=2009 
```

### hämta Book som inte finns

```bash
http GET :8080/api/v1/books/999 
```

### Skapa Loan men boken saknas

```bash
http POST :8080/api/v1/loans bookId:=999 borrower="Rasha"
```

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


---

## Utvecklare

Namn: Rasha Knifdi  
Kurs:  Backend API – Chas Academy
