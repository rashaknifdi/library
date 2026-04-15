package com.rasha.library.controller.v1;

import com.rasha.library.dto.v1.AuthorRequest;
import com.rasha.library.dto.v1.AuthorResponse;
import com.rasha.library.dto.v1.BookResponse;
import com.rasha.library.model.Author;
import com.rasha.library.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @PostMapping
    public AuthorResponse create(@Valid @RequestBody AuthorRequest req) {
        Author a = service.create(req.getName());
        return new AuthorResponse(a.getId(), a.getName());
    }

    @GetMapping("/{id}")
    public AuthorResponse get(@PathVariable Long id) {
        Author a = service.find(id);
        return new AuthorResponse(a.getId(), a.getName());
    }
    @GetMapping("/{id}/books")
    public List<BookResponse> getBooksByAuthor(@PathVariable Long id) {

        Author author = service.find(id);

        return author.getBooks().stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublishedYear(),
                        author.getId(),
                        author.getName()
                ))
                .toList();
    }

}
