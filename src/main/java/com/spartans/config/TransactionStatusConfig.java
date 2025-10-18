package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.transaction-status")
public class TransactionStatusConfig {
    
    private String borrowed = "BORROWED";
    private String returned = "RETURNED";
    private String due = "DUE";
    
    // Getters and Setters
    public String getBorrowed() {
        return borrowed;
    }
    
    public void setBorrowed(String borrowed) {
        this.borrowed = borrowed;
    }
    
    public String getReturned() {
        return returned;
    }
    
    public void setReturned(String returned) {
        this.returned = returned;
    }
    
    public String getDue() {
        return due;
    }
    
    public void setDue(String due) {
        this.due = due;
    }
}
