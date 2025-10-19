package com.spartans.controller;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import com.spartans.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

  @Autowired private TransactionService transactionService;

  // Borrow a book
  @PostMapping("/book/{bookId}/borrow")
  public ResponseEntity<Transaction> borrowBook(
      @PathVariable Long bookId, HttpServletRequest request) {
    Transaction transaction = transactionService.borrowBook(UserContext.getUserId(), bookId);
    return ResponseEntity.ok(transaction);
  }

  // Return a book
  @PostMapping("/book/{bookId}/return")
  public ResponseEntity<Transaction> returnBook(
      @PathVariable Long bookId, HttpServletRequest request) {
    Transaction transaction = transactionService.returnBook(UserContext.getUserId(), bookId);
    return ResponseEntity.ok(transaction);
  }

  // Get all borrowed books for the logged-in student
  @GetMapping("/borrowed")
  public ResponseEntity<List<Transaction>> getBorrowedBooks(HttpServletRequest request) {
    List<Transaction> borrowedBooks = transactionService.getBorrowedBooks(UserContext.getUserId());
    return ResponseEntity.ok(borrowedBooks);
  }

  // Get overdue books
  @GetMapping("/overdue")
  public ResponseEntity<List<Transaction>> getOverdueBooks(HttpServletRequest request) {
    List<Transaction> overdueBooks = transactionService.getOverdueBooks(UserContext.getUserId());
    return ResponseEntity.ok(overdueBooks);
  }

  // Get borrowing history
  @GetMapping("/history")
  public ResponseEntity<List<Transaction>> getBorrowingHistory(HttpServletRequest request) {
    List<Transaction> history = transactionService.getBorrowingHistory(UserContext.getUserId());
    return ResponseEntity.ok(history);
  }

  // Check if student can borrow more books
  @GetMapping("/can-borrow")
  public ResponseEntity<Boolean> canBorrowMoreBooks(HttpServletRequest request) {
    boolean canBorrow = transactionService.canBorrowMoreBooks(UserContext.getUserId());
    return ResponseEntity.ok(canBorrow);
  }

  // Get all transactions (Admin-only)
  @GetMapping
  public ResponseEntity<List<Transaction>> getAllTransactions() {
    List<Transaction> transactions = transactionService.getAllTransactions();
    return ResponseEntity.ok(transactions);
  }

  // Get transactions by status (Admin-only)
  @GetMapping("/status/{status}")
  public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
    List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
    return ResponseEntity.ok(transactions);
  }

  // Get overdue transactions (Admin-only)
  @GetMapping("/overdue/all")
  public ResponseEntity<List<Transaction>> getOverdueTransactions() {
    List<Transaction> overdueTransactions = transactionService.getOverdueTransactions();
    return ResponseEntity.ok(overdueTransactions);
  }

  // TODO: move to book controller
  // Get available books
  @GetMapping("/books/available")
  public ResponseEntity<List<Book>> getAvailableBooks() {
    List<Book> availableBooks = transactionService.getAvailableBooks();
    return ResponseEntity.ok(availableBooks);
  }

  // Update book inventory (Admin-only)
  @PutMapping("/books/{bookId}/inventory")
  public ResponseEntity<Book> updateBookInventory(
      @PathVariable Long bookId, @RequestParam Integer quantityChange) {
    Book updatedBook = transactionService.updateBookInventory(bookId, quantityChange);
    return ResponseEntity.ok(updatedBook);
  }

  // Check if a specific book is available
  @GetMapping("/books/{bookId}/availability")
  public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long bookId) {
    boolean isAvailable = transactionService.isBookAvailable(bookId);
    return ResponseEntity.ok(isAvailable);
  }

  // Update book availability (Admin-only)
  @PutMapping("/books/{bookId}/availability")
  public ResponseEntity<Book> updateBookAvailability(
      @PathVariable Long bookId, @RequestParam String availabilityStatus) {
    Book updatedBook = transactionService.updateBookAvailability(bookId, availabilityStatus);
    return ResponseEntity.ok(updatedBook);
  }

  // Get books with low stock (Admin-only)
  @GetMapping("/books/low-stock")
  public ResponseEntity<List<Book>> getBooksWithLowStock(
      @RequestParam(required = false) Integer threshold) {
    List<Book> lowStockBooks = transactionService.getBooksWithLowStock(threshold);
    return ResponseEntity.ok(lowStockBooks);
  }

  //
  //    @PostMapping("/book/{bookId}/borrow")
  //    public ResponseEntity<Transaction> borrowBook(@PathVariable Long bookId, HttpServletRequest
  // request) {
  //        String token = extractToken(request);
  //        Long userId = jwtUtils.getUserId(token);
  //        Transaction transaction = transactionService.borrowBook(userId, bookId);
  //        return ResponseEntity.ok(transaction);
  //    }

}
