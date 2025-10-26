package com.spartans.controller;

import com.spartans.config.AdminReportConfig;
import com.spartans.dto.BorrowBooksRequest;
import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import com.spartans.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

  @Autowired private TransactionService transactionService;

  @Autowired private AdminReportConfig config;

  // Borrow a single book
  @PostMapping("/book/{bookId}/borrow")
  public ResponseEntity<BorrowedBookDTO> borrowBook(
      @PathVariable Long bookId, HttpServletRequest request) {
    Transaction transaction = transactionService.borrowBook(UserContext.getUserId(), bookId);
    BorrowedBookDTO transactionDTO = mapToDTO(transaction);
    return ResponseEntity.ok(transactionDTO);
  }

  // Borrow multiple books
  @PostMapping("/books/borrow")
  public ResponseEntity<BorrowBooksResponse> borrowMultipleBooks(
      @Valid @RequestBody BorrowBooksRequest request, HttpServletRequest httpRequest) {
    Long userId = UserContext.getUserId();
    BorrowBooksResponse response =
        transactionService.borrowMultipleBooks(userId, request.getBookIds());
    return ResponseEntity.ok(response);
  }

  // Return a book
  @PostMapping("/book/{bookId}/return")
  public ResponseEntity<BorrowedBookDTO> returnBook(
      @PathVariable Long bookId, HttpServletRequest request) {
    BorrowedBookDTO returnedDTO = transactionService.returnBook(UserContext.getUserId(), bookId);
    return ResponseEntity.ok(returnedDTO);
  }

  // Get all borrowed books for the logged-in student
  @GetMapping("/borrowed")
  public ResponseEntity<List<BorrowedBookDTO>> getBorrowedBooks(HttpServletRequest request) {
    List<BorrowedBookDTO> borrowedBooks =
        transactionService.getBorrowedBooks(UserContext.getUserId()).stream()
            .map(this::mapToDTO)
            .toList();
    return ResponseEntity.ok(borrowedBooks);
  }

  // Get overdue books for the logged-in student
  @GetMapping("/overdue")
  public ResponseEntity<List<BorrowedBookDTO>> getOverdueBooks(HttpServletRequest request) {
    List<BorrowedBookDTO> overdueBooks =
        transactionService.getOverdueBooks(UserContext.getUserId()).stream()
            .map(this::mapToDTO)
            .toList();
    return ResponseEntity.ok(overdueBooks);
  }

  // Get borrowing history for the logged-in student
  @GetMapping("/history")
  public ResponseEntity<List<BorrowedBookDTO>> getBorrowingHistory(HttpServletRequest request) {
    List<BorrowedBookDTO> history =
        transactionService.getBorrowingHistory(UserContext.getUserId()).stream()
            .map(this::mapToDTO)
            .toList();
    return ResponseEntity.ok(history);
  }

  // Check if student can borrow more books
  @GetMapping("/can-borrow")
  public ResponseEntity<Boolean> canBorrowMoreBooks(HttpServletRequest request) {
    boolean canBorrow = transactionService.canBorrowMoreBooks(UserContext.getUserId());
    return ResponseEntity.ok(canBorrow);
  }

  // Admin-only: get all transactions
  @GetMapping
  public ResponseEntity<List<BorrowedBookDTO>> getAllTransactions() {
    UserContext.checkAdmin();
    List<BorrowedBookDTO> transactions =
        transactionService.getAllTransactions().stream().map(this::mapToDTO).toList();
    return ResponseEntity.ok(transactions);
  }

  // Admin-only: get transactions by status using request param
  @GetMapping("/status")
  public ResponseEntity<List<BorrowedBookDTO>> getTransactionsByStatus(
      @RequestParam String status) {
    UserContext.checkAdmin();
    List<BorrowedBookDTO> transactions =
        transactionService.getTransactionsByStatus(status).stream().map(this::mapToDTO).toList();
    return ResponseEntity.ok(transactions);
  }

  // Users with total fine above a threshold : /high-fines?fineThreshold=500
  @GetMapping("/high-fines")
  public ResponseEntity<?> getHighFineUsers(@RequestParam(required = false) Double fineThreshold) {
    UserContext.checkAdmin();
    double threshold = (fineThreshold != null) ? fineThreshold : config.getFineThreshold();
    return ResponseEntity.ok(transactionService.getHighFineUsers(threshold));
  }

  // Users with repeated late returns : /late-users?lateThreshold=3
  @GetMapping("/late-users")
  public ResponseEntity<?> getRepeatedLateUsers(
      @RequestParam(required = false) Long lateThreshold) {
    UserContext.checkAdmin();
    long threshold = (lateThreshold != null) ? lateThreshold : config.getLateThreshold();
    return ResponseEntity.ok(transactionService.getRepeatedLateUsers(threshold));
  }

  // Get available books
  @GetMapping("/books/available")
  public ResponseEntity<List<Book>> getAvailableBooks() {
    List<Book> availableBooks = transactionService.getAvailableBooks();
    return ResponseEntity.ok(availableBooks);
  }

  // Check if a specific book is available
  @GetMapping("/books/{bookId}/availability")
  public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long bookId) {
    UserContext.checkAdmin();
    boolean isAvailable = transactionService.isBookAvailable(bookId);
    return ResponseEntity.ok(isAvailable);
  }

  // Update book inventory (Admin-only)
  @PutMapping("/books/{bookId}/inventory")
  public ResponseEntity<Book> updateBookInventory(
      @PathVariable Long bookId, @RequestParam Integer quantityChange) {
    UserContext.checkAdmin();
    Book updatedBook = transactionService.updateBookInventory(bookId, quantityChange);
    return ResponseEntity.ok(updatedBook);
  }

  // Update book availability (Admin-only)
  @PutMapping("/books/{bookId}/availability")
  public ResponseEntity<Book> updateBookAvailability(
      @PathVariable Long bookId, @RequestParam String availabilityStatus) {
    UserContext.checkAdmin();
    Book updatedBook = transactionService.updateBookAvailability(bookId, availabilityStatus);
    return ResponseEntity.ok(updatedBook);
  }

  // Get books with low stock (Admin-only)
  @GetMapping("/books/low-stock")
  public ResponseEntity<List<Book>> getBooksWithLowStock(
      @RequestParam(required = false) Integer threshold) {
    UserContext.checkAdmin();
    List<Book> lowStockBooks = transactionService.getBooksWithLowStock(threshold);
    return ResponseEntity.ok(lowStockBooks);
  }

  // Helper method to convert Transaction to BorrowedBookDTO
  private BorrowedBookDTO mapToDTO(Transaction transaction) {
    return new BorrowedBookDTO(
        transaction.getTransactionId(),
        transaction.getBook().getBookId(),
        transaction.getBook().getBookTitle(),
        transaction.getUser().getUserId(),
        transaction.getBorrowDate(),
        transaction.getDueDate(),
        transaction.getReturnDate(),
        transaction.getFineAmount(),
            transaction.getTransactionStatus());
  }
}
