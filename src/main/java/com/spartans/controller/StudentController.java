package com.spartans.controller;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    // Borrow a book
    @PostMapping("/{userId}/borrow/{bookId}")
    public ResponseEntity<Transaction> borrowBook(@PathVariable Long userId, @PathVariable Long bookId) {
        Transaction transaction = studentService.borrowBook(userId, bookId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
    
    // Return a book
    @PostMapping("/{userId}/return/{bookId}")
    public ResponseEntity<Transaction> returnBook(@PathVariable Long userId, @PathVariable Long bookId) {
        Transaction transaction = studentService.returnBook(userId, bookId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
    
    // Get all borrowed books for a student
    @GetMapping("/{userId}/borrowed-books")
    public ResponseEntity<List<Transaction>> getBorrowedBooks(@PathVariable Long userId) {
        List<Transaction> borrowedBooks = studentService.getBorrowedBooks(userId);
        return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
    }
    
    // Get overdue books for a student
    @GetMapping("/{userId}/overdue-books")
    public ResponseEntity<List<Transaction>> getOverdueBooks(@PathVariable Long userId) {
        List<Transaction> overdueBooks = studentService.getOverdueBooks(userId);
        return new ResponseEntity<>(overdueBooks, HttpStatus.OK);
    }
    
    // Get borrowing history for a student
    @GetMapping("/{userId}/borrowing-history")
    public ResponseEntity<List<Transaction>> getBorrowingHistory(@PathVariable Long userId) {
        List<Transaction> history = studentService.getBorrowingHistory(userId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
    
    // Check if student can borrow more books
    @GetMapping("/{userId}/can-borrow")
    public ResponseEntity<Boolean> canBorrowMoreBooks(@PathVariable Long userId) {
        boolean canBorrow = studentService.canBorrowMoreBooks(userId);
        return new ResponseEntity<>(canBorrow, HttpStatus.OK);
    }
    
    // Get available books
    @GetMapping("/available-books")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        List<Book> availableBooks = studentService.getAvailableBooks();
        return new ResponseEntity<>(availableBooks, HttpStatus.OK);
    }
    
    // Check if specific book is available
    @GetMapping("/books/{bookId}/availability")
    public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long bookId) {
        boolean isAvailable = studentService.isBookAvailable(bookId);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }
}
