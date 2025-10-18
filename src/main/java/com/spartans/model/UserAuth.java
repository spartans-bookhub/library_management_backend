package com.spartans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Authentication")
public class UserAuth {

    @Id
    private String loginId;
    private String password;
    private String role;  //STUDENT/ ADMIN

    @OneToOne(mappedBy = "userAuth", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private User student;

    public UserAuth() {
    }

    public UserAuth(String loginId, String password, String role, User student) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.student = student;
    }


    public UserAuth(String loginId, String password, String role) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
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
}