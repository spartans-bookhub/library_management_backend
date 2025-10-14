package com.spartans.controller;

import com.spartans.config.LibraryConfig;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.AdminService;
import com.spartans.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private LibraryConfig libraryConfig;

    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }
    
    // Update book inventory
    @PutMapping("/books/{bookId}/inventory")
    public ResponseEntity<Book> updateBookInventory(@PathVariable Long bookId, @RequestParam Integer quantityChange) {
        Book updatedBook = adminService.updateBookInventory(bookId, quantityChange);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }
    
    // Update book availability
    @PutMapping("/books/{bookId}/availability")
    public ResponseEntity<Book> updateBookAvailability(@PathVariable Long bookId, @RequestParam String availabilityStatus) {
        Book updatedBook = adminService.updateBookAvailability(bookId, availabilityStatus);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }
    
    // Get all transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = adminService.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
    
    // Get transactions by status
    @GetMapping("/transactions/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        List<Transaction> transactions = adminService.getTransactionsByStatus(status);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
    
    // Get overdue transactions
    @GetMapping("/transactions/overdue")
    public ResponseEntity<List<Transaction>> getOverdueTransactions() {
        List<Transaction> overdueTransactions = adminService.getOverdueTransactions();
        return new ResponseEntity<>(overdueTransactions, HttpStatus.OK);
    }
    
    // Get books with low stock
    @GetMapping("/books/low-stock")
    public ResponseEntity<List<Book>> getBooksWithLowStock(@RequestParam(required = false) Integer threshold) {
        List<Book> lowStockBooks = adminService.getBooksWithLowStock(threshold);
        return new ResponseEntity<>(lowStockBooks, HttpStatus.OK);
    }
}
