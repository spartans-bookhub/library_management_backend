package com.spartans.service;

import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import java.util.List;

public interface TransactionService {
  // Borrow a book
  Transaction borrowBook(Long userId, Long bookId);

  // Return a book
  BorrowedBookDTO returnBook(Long userId, Long bookId);

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

  // ---------Admin methods---------------
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

  // Borrow multiple books
  BorrowBooksResponse borrowMultipleBooks(Long userId, List<Long> bookIds);
}
