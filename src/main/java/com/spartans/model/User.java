package com.spartans.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  private String userName;
  private String userEmail;
  private String userRole; // ADMIN or STUDENT
  private String userPassword;
  private LocalDateTime createdAt;
  private String phone;
  private String address;

  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "auth_login_id", referencedColumnName = "loginId")
  private UserAuth userAuth;

  // Default constructor for JPA
  public User() {}

  public User(String userName, String userEmail, LocalDateTime createdAt) {
    this.userName = userName;
    this.userEmail = userEmail;
    this.createdAt = createdAt;
  }

  public User(
      Long userId,
      String userName,
      String userEmail,
      String userRole,
      String userPassword,
      LocalDateTime createdAt,
      String phone,
      String address,
      UserAuth userAuth) {
    this.userId = userId;
    this.userName = userName;
    this.userEmail = userEmail;
    this.userRole = userRole;
    this.userPassword = userPassword;
    this.createdAt = createdAt;
    this.phone = phone;
    this.address = address;
    this.userAuth = userAuth;
  }

  public UserAuth getUserAuth() {
    return userAuth;
  }

  public void setUserAuth(UserAuth userAuth) {
    this.userAuth = userAuth;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserRole() {
    return userRole;
  }

  public void setUserRole(String userRole) {
    this.userRole = userRole;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
