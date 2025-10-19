package com.spartans.dto;

public class ErrorResponseDTO {
  private String message;
  private int status;

  public ErrorResponseDTO(String message, int status) {
    this.message = message;
    this.status = status;
  }

  public ErrorResponseDTO(int status, String message, String requestURI) {}

  // Getters and setters
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
