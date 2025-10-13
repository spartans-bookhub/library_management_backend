package com.spartans.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Authentication")
public class UserAuth {

    @Id
    private String loginId;
    private String password;

    private String role;   //STUDENT or ADMIN

    // Bidirectional relation with Student
    @OneToOne(mappedBy = "auth", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Student student;

    public UserAuth() {
    }

    public UserAuth(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
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
}
