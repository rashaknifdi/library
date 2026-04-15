package com.rasha.library.controller.v2;

import com.rasha.library.dto.v2.BookListResponse;
import com.rasha.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books API v2", description = "Version 2 of the Books API with wrapper response")
@RestController
@RequestMapping("/api/v2/books")
public class BookControllerV2 {

    private final BookService service;

    public BookControllerV2(BookService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get all books (v2)",
            description = "Returns books wrapped in a data object with version information."
    )
    @GetMapping
    public BookListResponse getAll() {
        return service.getAllV2();
    }
}
