package com.rasha.library.dto.v1;

public class LoanRequest {

    private Long bookId;
    private String borrower;

    public LoanRequest() {}

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }


}