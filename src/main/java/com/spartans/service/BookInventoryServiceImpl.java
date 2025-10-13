package com.spartans.service;

import com.spartans.exception.BookNotAvailableException;
import com.spartans.model.BookInventory;
import com.spartans.repository.BookInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookInventoryServiceImpl implements BookInventoryService {

    @Autowired
    private BookInventoryRepository bookInventoryRepository;

    @Override
    public boolean isAvailable(Long bookId) {
        BookInventory inventory = bookInventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new BookNotAvailableException("Book inventory not found"));
        return inventory.getAvailableCopies() > 0;
    }

    @Override
    public void decrementStock(Long bookId) {
        BookInventory inventory = bookInventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new BookNotAvailableException("Book inventory not found"));
        if (inventory.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book is out of stock");
        }
        inventory.setAvailableCopies(inventory.getAvailableCopies() - 1);
        bookInventoryRepository.save(inventory);
    }

    @Override
    public void incrementStock(Long bookId) {
        BookInventory inventory = bookInventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new RuntimeException("Book BookInventory not found"));
        inventory.setAvailableCopies(inventory.getAvailableCopies() + 1);
        bookInventoryRepository.save(inventory);
    }
}

