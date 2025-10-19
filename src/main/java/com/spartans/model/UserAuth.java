package com.spartans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Authentication")
public class UserAuth {

  @Id private String email;
  private String password;
  private String role; // STUDENT/ ADMIN
  private String resetToken;

  @OneToOne(
      mappedBy = "userAuth",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private User student;

  public UserAuth() {}

  public UserAuth(String email, String password, String role, String resetToken, User student) {
    this.email = email;
    this.password = password;
    this.role = role;
    this.resetToken = resetToken;
    this.student = student;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public User getStudent() {
    return student;
  }

  public void setStudent(User student) {
    this.student = student;
  }

  public String getResetToken() {
    return resetToken;
  }

  public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
  }
}
