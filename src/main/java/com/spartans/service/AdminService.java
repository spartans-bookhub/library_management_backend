package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;

import java.util.List;

public interface AdminService {
    
    // Update book inventory when book is returned
    Book updateBookInventory(Long bookId, Integer quantityChange);
    
    // Get all transactions
    List<Transaction> getAllTransactions();
    
    // Get transactions by status
    List<Transaction> getTransactionsByStatus(String status);
    
    // Get overdue transactions
    List<Transaction> getOverdueTransactions();
    
    // Update book availability status
    Book updateBookAvailability(Long bookId, String availabilityStatus);
    
    // Get all books with low stock
    List<Book> getBooksWithLowStock(Integer threshold);
}
