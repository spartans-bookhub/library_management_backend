package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library.book-availability")
public class BookAvailabilityConfig {
    
    private String available = "YES";
    private String unavailable = "NO";
    
    // Getters and Setters
    public String getAvailable() {
        return available;
    }
    
    public void setAvailable(String available) {
        this.available = available;
    }
    
    public String getUnavailable() {
        return unavailable;
    }
    
    public void setUnavailable(String unavailable) {
        this.unavailable = unavailable;
    }
}
