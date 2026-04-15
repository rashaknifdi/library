package com.rasha.library.dto.v1;

import jakarta.validation.constraints.NotBlank;

public class AuthorRequest {
    @NotBlank
    public String name;

    public AuthorRequest() {}
    public AuthorRequest(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
