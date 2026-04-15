package com.rasha.library.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Book response model for API v2 including availability")
public class BookResponse {

    private String title;
    private String author;
    private boolean available;

    public BookResponse(String title, String author, boolean available) {
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
}
