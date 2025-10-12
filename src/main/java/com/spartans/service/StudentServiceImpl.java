package com.spartans.service;

import com.spartans.config.LibraryConfig;
import com.spartans.config.UserRoleConfig;
import com.spartans.config.TransactionStatusConfig;
import com.spartans.config.BookAvailabilityConfig;
import com.spartans.exception.*;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.repository.BookRepository;
import com.spartans.repository.TransactionRepository;
import com.spartans.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private LibraryConfig libraryConfig;
    
    @Autowired
    private UserRoleConfig userRoleConfig;
    
    @Autowired
    private TransactionStatusConfig transactionStatusConfig;
    
    @Autowired
    private BookAvailabilityConfig bookAvailabilityConfig;
    
    @Override
    public Transaction borrowBook(Long userId, Long bookId) {
        // Check if user exists and is a student
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!userRoleConfig.getStudent().equals(user.getUserRole())) {
            throw new InvalidOperationException("Only students can borrow books");
        }
        
        // Check if book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        // Check if book is available
        if (!isBookAvailable(bookId)) {
            throw new BookNotAvailableException("Book is not available for borrowing");
        }
        
        // Check if student has already borrowed this book
        Optional<Transaction> existingBorrow = transactionRepository
                .findByUserAndBookAndTransactionStatus(user, book, transactionStatusConfig.getBorrowed());
        if (existingBorrow.isPresent()) {
            throw new BookAlreadyBorrowedException("You have already borrowed this book");
        }
        
        // Check borrowing limit
        if (!canBorrowMoreBooks(userId)) {
            throw new BorrowLimitExceededException("You have reached the maximum borrowing limit of " + libraryConfig.getMaxBorrowLimit() + " books");
        }
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(libraryConfig.getBorrowPeriodDays()));
        transaction.setTransactionStatus(transactionStatusConfig.getBorrowed());
        transaction.setFineAmount(0.0);
        
        // Update book quantity
        book.setQuantity(book.getQuantity() - 1);
        if (book.getQuantity() <= 0) {
            book.setAvailabilityStatus(bookAvailabilityConfig.getUnavailable());
        }
        bookRepository.save(book);
        
        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Send notification
        notificationService.sendBookBorrowedNotification(user, book);
        
        return savedTransaction;
    }
    
    @Override
    public Transaction returnBook(Long userId, Long bookId) {
        // Find the active transaction
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        
        Transaction transaction = transactionRepository
                .findByUserAndBookAndTransactionStatus(user, book, transactionStatusConfig.getBorrowed())
                .orElseThrow(() -> new ResourceNotFoundException("No active borrowing found for this book"));
        
        // Update transaction
        LocalDate returnDate = LocalDate.now();
        transaction.setReturnDate(returnDate);
        transaction.setTransactionStatus(transactionStatusConfig.getReturned());
        
        // Calculate fine if late
        if (returnDate.isAfter(transaction.getDueDate())) {
            long daysLate = returnDate.toEpochDay() - transaction.getDueDate().toEpochDay();
            double fine = daysLate * libraryConfig.getDailyFineRate();
            transaction.setFineAmount(fine);
            transaction.setPenaltyReason("Late return - " + daysLate + " days overdue");
            
            // Send late return notification
            notificationService.sendLateReturnNotification(user, book, daysLate, fine);
        }
        
        // Update book quantity
        book.setQuantity(book.getQuantity() + 1);
        if (book.getQuantity() > 0) {
            book.setAvailabilityStatus(bookAvailabilityConfig.getAvailable());
        }
        bookRepository.save(book);
        
        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Send return notification
        notificationService.sendBookReturnedNotification(user, book);
        
        return savedTransaction;
    }
    
    @Override
    public List<Transaction> getBorrowedBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return transactionRepository.findByUserAndTransactionStatus(user, transactionStatusConfig.getBorrowed());
    }
    
    @Override
    public List<Transaction> getOverdueBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        LocalDate today = LocalDate.now();
        List<Transaction> borrowedBooks = transactionRepository.findByUserAndTransactionStatus(user, transactionStatusConfig.getBorrowed());
        
        return borrowedBooks.stream()
                .filter(transaction -> today.isAfter(transaction.getDueDate()))
                .toList();
    }
    
    @Override
    public List<Transaction> getBorrowingHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return transactionRepository.findByUser(user);
    }
    
    @Override
    public boolean canBorrowMoreBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        long currentBorrowedCount = transactionRepository.countByUserAndTransactionStatus(user, transactionStatusConfig.getBorrowed());
        return currentBorrowedCount < libraryConfig.getMaxBorrowLimit();
    }
    
    @Override
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailabilityStatus(bookAvailabilityConfig.getAvailable());
    }
    
    @Override
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.isPresent() && 
               bookAvailabilityConfig.getAvailable().equals(book.get().getAvailabilityStatus()) && 
               book.get().getQuantity() > 0;
    }
}
