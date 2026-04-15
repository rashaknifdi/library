package com.rasha.library.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Book creation request model for API v1")
public class BookRequest {
    @NotBlank
    private String title;
    private String isbn;
    @NotNull
    private int publishedYear;
    @NotNull
    private Long authorId;

    public  BookRequest() {}

    public BookRequest(String title, String isbn, int publishedYear, Long authorId) {
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.authorId = authorId;
    }


    // getters & setters


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
