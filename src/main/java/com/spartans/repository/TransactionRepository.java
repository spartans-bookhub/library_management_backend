package com.spartans.repository;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find transactions by user and status
    List<Transaction> findByUserAndTransactionStatus(User user, String transactionStatus);
    
    // Find transactions by user
    List<Transaction> findByUser(User user);
    
    // Find active transaction for a specific user and book
    Optional<Transaction> findByUserAndBookAndTransactionStatus(User user, Book book, String transactionStatus);
    
    // Count active borrowings for a user
    long countByUserAndTransactionStatus(User user, String transactionStatus);
    
    // Find transactions by status
    List<Transaction> findByTransactionStatus(String transactionStatus);
}
