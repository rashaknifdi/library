package com.rasha.library.controller.v1;

import com.rasha.library.dto.v1.AuthorRequest;
import com.rasha.library.dto.v1.AuthorResponse;
import com.rasha.library.dto.v1.BookResponse;
import com.rasha.library.model.Author;
import com.rasha.library.service.AuthorService;
import com.rasha.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService service;
    private final BookService bookService;

    public AuthorController(AuthorService service, BookService bookService) {
        this.service = service;
        this.bookService = bookService;
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

    @GetMapping
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return service.findAll(pageable).map(a -> new AuthorResponse(a.getId(), a.getName()));
    }

    @GetMapping("/{id}/books")
    public Page<BookResponse> getBooksByAuthor(@PathVariable Long id, Pageable pageable) {
        return bookService.getByAuthor(id, pageable);
    }

}
