package com.spartans.exception;

public class InvalidLoginException extends RuntimeException {
  public InvalidLoginException(String message) {
    super(message);
  }
}
