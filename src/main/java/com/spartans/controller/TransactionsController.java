package com.spartans.controller;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionsController {
    @Autowired
    private TransactionService transactionService;

    // Borrow a book
    @PostMapping("/{userId}/borrow/{bookId}")
    public ResponseEntity<Transaction> borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        Transaction transaction = transactionService.borrowBook(userId, bookId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // Return a book
    @PostMapping("/{userId}/return/{bookId}")
    public ResponseEntity<Transaction> returnBook(@PathVariable Long userId, @PathVariable Long bookId) {
        Transaction transaction = transactionService.returnBook(userId, bookId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // Get all borrowed books for a student
    @GetMapping("/{userId}/borrowed-books")
    public ResponseEntity<List<Transaction>> getBorrowedBooks(@PathVariable Long userId) {
        List<Transaction> borrowedBooks = transactionService.getBorrowedBooks(userId);
        return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
    }

    // Get overdue books for a student
    @GetMapping("/{userId}/overdue-books")
    public ResponseEntity<List<Transaction>> getOverdueBooks(@PathVariable Long userId) {
        List<Transaction> overdueBooks = transactionService.getOverdueBooks(userId);
        return new ResponseEntity<>(overdueBooks, HttpStatus.OK);
    }

    // Get borrowing history for a student
    @GetMapping("/{userId}/borrowing-history")
    public ResponseEntity<List<Transaction>> getBorrowingHistory(@PathVariable Long userId) {
        List<Transaction> history = transactionService.getBorrowingHistory(userId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    // Check if student can borrow more books
    @GetMapping("/{userId}/can-borrow")
    public ResponseEntity<Boolean> canBorrowMoreBooks(@PathVariable Long userId) {
        boolean canBorrow = transactionService.canBorrowMoreBooks(userId);
        return new ResponseEntity<>(canBorrow, HttpStatus.OK);
    }

    // Get available books
    @GetMapping("/available-books")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> availableBooks = transactionService.getAvailableBooks();
        return new ResponseEntity<>(availableBooks, HttpStatus.OK);
    }

    // Check if specific book is available
    @GetMapping("/books/{bookId}/availability")
    public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long bookId) {
        boolean isAvailable = transactionService.isBookAvailable(bookId);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }
    

    // Update book inventory
    @PutMapping("/books/{bookId}")
    public ResponseEntity<Book> updateBookInventory(@PathVariable Long bookId, @RequestParam Integer quantityChange) {
        Book updatedBook = transactionService.updateBookInventory(bookId, quantityChange);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    // Update book availability
    @PutMapping("/books/{bookId}/availability")
    public ResponseEntity<Book> updateBookAvailability(@PathVariable Long bookId, @RequestParam String availabilityStatus) {
        Book updatedBook = transactionService.updateBookAvailability(bookId, availabilityStatus);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    // Get all transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Get transactions by status
    @GetMapping("/transactions/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // Get overdue transactions
    @GetMapping("/transactions/overdue")
    public ResponseEntity<List<Transaction>> getOverdueTransactions() {
        List<Transaction> overdueTransactions = transactionService.getOverdueTransactions();
        return new ResponseEntity<>(overdueTransactions, HttpStatus.OK);
    }

    // Get books with low stock
    @GetMapping("/books/low-stock")
    public ResponseEntity<List<Book>> getBooksWithLowStock(@RequestParam(required = false) Integer threshold) {
        List<Book> lowStockBooks = transactionService.getBooksWithLowStock(threshold);
        return new ResponseEntity<>(lowStockBooks, HttpStatus.OK);
    }
}
