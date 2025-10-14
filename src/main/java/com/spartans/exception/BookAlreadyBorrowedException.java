package com.spartans.exception;

public class BookAlreadyBorrowedException extends RuntimeException {
    public BookAlreadyBorrowedException(String message) {
        super(message);
    }
    
    public BookAlreadyBorrowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
