package com.rasha.library.dto.v1;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class LoanRequest {

    @NotNull(message = "bookId is required")
    private Long bookId;

    @NotBlank(message = "borrower is required")
    private String borrower;

    public LoanRequest() {}

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBorrower() { return borrower; }
    public void setBorrower(String borrower) { this.borrower = borrower; }
}
