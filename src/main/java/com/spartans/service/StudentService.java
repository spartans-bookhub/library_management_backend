package com.spartans.service;

import com.spartans.model.Transaction;

import java.util.List;

public interface StudentService {
    Transaction borrowBook(Long studentId, Long bookId);
    Transaction returnBook(Long studentId, Long bookId);
    List<Transaction> getBorrowedBooks(Long studentId);
    List<Transaction> getBorrowHistory(Long studentId);
}
