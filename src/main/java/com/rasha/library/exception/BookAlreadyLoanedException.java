package com.rasha.library.exception;

public class BookAlreadyLoanedException extends RuntimeException {
    public BookAlreadyLoanedException(Long bookId) {
        super("Book with id " + bookId + " is already loaned");
    }
}
