package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;

import java.util.List;

public interface StudentService {
    
    // Borrow a book
    Transaction borrowBook(Long userId, Long bookId);
    
    // Return a book
    Transaction returnBook(Long userId, Long bookId);
    
    // Get all borrowed books for a student
    List<Transaction> getBorrowedBooks(Long userId);
    
    // Get overdue books for a student
    List<Transaction> getOverdueBooks(Long userId);
    
    // Get borrowing history for a student
    List<Transaction> getBorrowingHistory(Long userId);
    
    // Check if student can borrow more books
    boolean canBorrowMoreBooks(Long userId);
    
    // Get available books
    List<Book> getAvailableBooks();
    
    // Check if book is available
    boolean isBookAvailable(Long bookId);
}
