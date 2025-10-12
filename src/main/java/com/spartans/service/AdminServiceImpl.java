package com.spartans.service;

import com.spartans.config.LibraryConfig;
import com.spartans.config.TransactionStatusConfig;
import com.spartans.config.BookAvailabilityConfig;
import com.spartans.exception.ResourceNotFoundException;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.repository.BookRepository;
import com.spartans.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private LibraryConfig libraryConfig;
    
    @Autowired
    private TransactionStatusConfig transactionStatusConfig;
    
    @Autowired
    private BookAvailabilityConfig bookAvailabilityConfig;
    
    @Override
    public Book updateBookInventory(Long bookId, Integer quantityChange) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        int newQuantity = book.getQuantity() + quantityChange;
        book.setQuantity(newQuantity);
        
        // Update availability status based on new quantity
        if (newQuantity <= 0) {
            book.setAvailabilityStatus(bookAvailabilityConfig.getUnavailable());
        } else {
            book.setAvailabilityStatus(bookAvailabilityConfig.getAvailable());
        }
        
        return bookRepository.save(book);
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByTransactionStatus(status);
    }
    
    @Override
    public List<Transaction> getOverdueTransactions() {
        LocalDate today = LocalDate.now();
        List<Transaction> borrowedTransactions = transactionRepository.findByTransactionStatus(transactionStatusConfig.getBorrowed());
        
        return borrowedTransactions.stream()
                .filter(transaction -> today.isAfter(transaction.getDueDate()))
                .toList();
    }
    
    @Override
    public Book updateBookAvailability(Long bookId, String availabilityStatus) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        book.setAvailabilityStatus(availabilityStatus);
        return bookRepository.save(book);
    }
    
    @Override
    public List<Book> getBooksWithLowStock(Integer threshold) {
        int actualThreshold = threshold != null ? threshold : libraryConfig.getLowStockThreshold();
        return bookRepository.findAll().stream()
                .filter(book -> book.getQuantity() <= actualThreshold)
                .toList();
    }
}
