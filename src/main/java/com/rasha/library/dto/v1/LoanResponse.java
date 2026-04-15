package com.rasha.library.dto.v1;

public class LoanResponse {

    private Long id;
    private Long bookId;
    private String borrower;

    public LoanResponse(Long id, Long bookId, String borrower) {
        this.id = id;
        this.bookId = bookId;
        this.borrower = borrower;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBorrower() {
        return borrower;
    }
}
