package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.user-roles")
public class UserRoleConfig {
    
    private String student = "STUDENT";
    private String admin = "ADMIN";
    
    // Getters and Setters
    public String getStudent() {
        return student;
    }
    
    public void setStudent(String student) {
        this.student = student;
    }
    
    public String getAdmin() {
        return admin;
    }
    
    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
