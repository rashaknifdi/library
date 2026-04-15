package com.rasha.library.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Wrapper object for API v2 responses")
public class BookListResponse {

    private List<BookResponse> data;
    private String version = "v2";

    public BookListResponse(List<BookResponse> data) {
        this.data = data;
    }

    public List<BookResponse> getData() { return data; }
    public String getVersion() { return version; }
}
