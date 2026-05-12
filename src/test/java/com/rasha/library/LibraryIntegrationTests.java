package com.rasha.library;

import com.rasha.library.dto.v1.*;
import com.rasha.library.repository.AuthorRepository;
import com.rasha.library.repository.BookRepository;
import com.rasha.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.vault.enabled=false",
                "app.rate-limiting.enabled=false"
        }
)
public class LibraryIntegrationTests {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private AuthorRepository authorRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private LoanRepository loanRepo;

    @BeforeEach
    void setup() {
        rest = rest.withBasicAuth("admin", "password");

        loanRepo.deleteAll();
        bookRepo.deleteAll();
        authorRepo.deleteAll();
    }

    @Test
    void createAuthorAndBook() {
        AuthorRequest authorReq = new AuthorRequest("Frank Herbert");
        ResponseEntity<AuthorResponse> authorRes =
                rest.postForEntity("/api/v1/authors", authorReq, AuthorResponse.class);

        assertEquals(HttpStatus.OK, authorRes.getStatusCode());
        Long authorId = authorRes.getBody().getId();

        BookRequest bookReq = new BookRequest("Dune", "123", 1965, authorId);
        ResponseEntity<BookResponse> bookRes =
                rest.postForEntity("/api/v1/books", bookReq, BookResponse.class);

        assertEquals(HttpStatus.CREATED, bookRes.getStatusCode());
    }

    @Test
    void createLoan() {
        AuthorResponse author = rest.postForEntity(
                "/api/v1/authors", new AuthorRequest("A"), AuthorResponse.class
        ).getBody();

        BookResponse book = rest.postForEntity(
                "/api/v1/books", new BookRequest("B", "1", 2000, author.id), BookResponse.class
        ).getBody();

        LoanRequest loanReq = new LoanRequest();
        loanReq.setBookId(book.getId());
        loanReq.setBorrower("Rasha");

        ResponseEntity<LoanResponse> res =
                rest.postForEntity("/api/v1/loans", loanReq, LoanResponse.class);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void borrowBorrowedBook() {
        AuthorResponse author = rest.postForEntity(
                "/api/v1/authors", new AuthorRequest("A"), AuthorResponse.class
        ).getBody();

        BookResponse book = rest.postForEntity(
                "/api/v1/books", new BookRequest("B", "1", 2000, author.id), BookResponse.class
        ).getBody();

        LoanRequest req = new LoanRequest();
        req.setBookId(book.getId());
        req.setBorrower("X");

        rest.postForEntity("/api/v1/loans", req, String.class);
        ResponseEntity<String> res =
                rest.postForEntity("/api/v1/loans", req, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void getMissingBook() {
        ResponseEntity<String> res =
                rest.getForEntity("/api/v1/books/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }
}
