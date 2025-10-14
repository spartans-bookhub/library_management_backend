package com.spartans.exception;

public class BorrowLimitExceededException extends RuntimeException {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
    
    public BorrowLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
