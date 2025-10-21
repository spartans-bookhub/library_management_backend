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
  private LocalDateTime createdAt;
  private String contactNumber;
  private String address;

  @JsonIgnore
  @OneToOne
  @JoinColumn(name = "user_email", referencedColumnName = "email")
  private UserAuth userAuth;

  // Default constructor for JPA
  public User() {}

  public User(String userName, String userEmail, LocalDateTime createdAt) {
    this.userName = userName;
    this.createdAt = createdAt;
  }

  public User(
      Long userId,
      String userName,
      LocalDateTime createdAt,
      String contactNumber,
      String address,
      UserAuth userAuth) {
    this.userId = userId;
    this.userName = userName;
    this.createdAt = createdAt;
    this.contactNumber = contactNumber;
    this.address = address;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public UserAuth getUserAuth() {
    return userAuth;
  }

  public void setUserAuth(UserAuth userAuth) {
    this.userAuth = userAuth;
  }
}
