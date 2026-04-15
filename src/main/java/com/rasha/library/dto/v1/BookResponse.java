package com.rasha.library.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Book response model for API v1")

public class BookResponse {

    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Long authorId;
    private String authorName;

    public BookResponse(Long id,
                        String title,
                        String isbn,
                        Integer publishedYear,
                        Long authorId,
                        String authorName) {

        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.authorId = authorId;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }
}
