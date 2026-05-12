package com.rasha.library;

import com.rasha.library.dto.v1.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.vault.enabled=false",
                "app.rate-limiting.enabled=false"
        }
)
public class LoanConcurrencyTest {

    @Autowired
    private TestRestTemplate rest;

    private Long bookId;

    @BeforeEach
    void setup() {
        rest = rest.withBasicAuth("admin", "password");

        AuthorResponse author = rest.postForEntity(
                "/api/v1/authors",
                new AuthorRequest("Test"),
                AuthorResponse.class
        ).getBody();

        BookResponse book = rest.postForEntity(
                "/api/v1/books",
                new BookRequest("Book", "111", 2000, author.getId()),
                BookResponse.class
        ).getBody();

        this.bookId = book.getId();
    }

    @Test
    void onlyOneLoanShouldBeCreatedUnderHeavyLoad() throws Exception {

        int threads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<Future<ResponseEntity<String>>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                LoanRequest req = new LoanRequest();
                req.setBookId(bookId);
                req.setBorrower("User" + Thread.currentThread().getId());
                return rest.postForEntity("/api/v1/loans", req, String.class);
            }));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        long successCount = futures.stream()
                .map(f -> {
                    try { return f.get().getStatusCode().value(); }
                    catch (Exception e) { return 0; }
                })
                .filter(code -> code == 200)
                .count();

        assertEquals(1, successCount);
    }
}
