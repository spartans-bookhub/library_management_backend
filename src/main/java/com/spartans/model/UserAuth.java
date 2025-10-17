package com.spartans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "userAuth")
public class UserAuth {

    @Id
    private String email;
    private String password;
    private String role;  //STUDENT/ ADMIN

    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private User user;

    public UserAuth() {
    }

    public UserAuth(String email, String password, String role, User user) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.user = user;
    }


    public UserAuth(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getLoginId() {
        return email;
    }

    public void setLoginId(String loginId) {
        this.email = loginId;
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
        return user;
    }

    public void setStudent(User student) {
        this.user = user;
    }


}
