package com.spartans.controller;

import com.spartans.dto.BorrowBooksRequest;
import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.exception.UnauthorizedAccessException;
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

  // For both student & admin
  @GetMapping("/history")
  public ResponseEntity<List<BorrowedBookDTO>> getBorrowingHistory() {
    String role = UserContext.getRole();
    Long userId = UserContext.getUserId();

    List<Transaction> transactions;

    if ("ADMIN".equalsIgnoreCase(role)) {
      // Admin can view all borrowing history
      transactions = transactionService.getAllBorrowingHistory();
    } else if ("STUDENT".equalsIgnoreCase(role)) {
      // Students can view only their own
      transactions = transactionService.getBorrowingHistory(userId);
    } else {
      throw new UnauthorizedAccessException("Invalid user role: " + role);
    }

    List<BorrowedBookDTO> history = transactions.stream().map(this::mapToDTO).toList();

    return ResponseEntity.ok(history);
  }

  // For ADMIN: Get borrowing history of a specific student
  @GetMapping("/history/{userId}")
  public ResponseEntity<List<BorrowedBookDTO>> getBorrowingHistoryByUserId(
      @PathVariable Long userId) {
    String role = UserContext.getRole();

    if (!"ADMIN".equalsIgnoreCase(role)) {
      throw new UnauthorizedAccessException(
          "Access denied. Only admins can view another user's history.");
    }

    List<BorrowedBookDTO> history =
        transactionService.getBorrowingHistoryByUserId(userId).stream()
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
    List<BorrowedBookDTO> transactions =
        transactionService.getAllTransactions().stream().map(this::mapToDTO).toList();
    return ResponseEntity.ok(transactions);
  }

  // Admin-only: get transactions by status
  @GetMapping("/status/{status}")
  public ResponseEntity<List<BorrowedBookDTO>> getTransactionsByStatus(
      @PathVariable String status) {
    List<BorrowedBookDTO> transactions =
        transactionService.getTransactionsByStatus(status).stream().map(this::mapToDTO).toList();
    return ResponseEntity.ok(transactions);
  }

  // Admin-only: get overdue transactions
  @GetMapping("/overdue/all")
  public ResponseEntity<List<BorrowedBookDTO>> getOverdueTransactions() {
    List<BorrowedBookDTO> overdueTransactions =
        transactionService.getOverdueTransactions().stream().map(this::mapToDTO).toList();
    return ResponseEntity.ok(overdueTransactions);
  }

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
        transaction.getFineAmount());
  }
}
