package com.spartans.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "library")
public class LibraryConfig {
    
    private int maxBorrowLimit = 5;
    private int borrowPeriodDays = 14;
    private double dailyFineRate = 1.0;
    private int lowStockThreshold = 5;
    
    // Getters and Setters
    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }
    
    public void setMaxBorrowLimit(int maxBorrowLimit) {
        this.maxBorrowLimit = maxBorrowLimit;
    }
    
    public int getBorrowPeriodDays() {
        return borrowPeriodDays;
    }
    
    public void setBorrowPeriodDays(int borrowPeriodDays) {
        this.borrowPeriodDays = borrowPeriodDays;
    }
    
    public double getDailyFineRate() {
        return dailyFineRate;
    }
    
    public void setDailyFineRate(double dailyFineRate) {
        this.dailyFineRate = dailyFineRate;
    }
    
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
    
    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
