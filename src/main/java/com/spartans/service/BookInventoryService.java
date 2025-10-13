package com.spartans.service;

public interface BookInventoryService {
    boolean isAvailable(Long bookId);
    void decrementStock(Long bookId);
    void incrementStock(Long bookId);
}
