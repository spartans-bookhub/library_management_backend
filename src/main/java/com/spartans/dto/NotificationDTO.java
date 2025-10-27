package com.spartans.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
  private Long id;
  private String message;
  private LocalDateTime createdAt;

  public NotificationDTO(Long id, String message, LocalDateTime createdAt) {
    this.id = id != null ? id : 0L; // default 0 if null
    this.message = message != null ? message : "";
    this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id != null ? id : 0L;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message != null ? message : "";
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
  }
}
