package com.spartans.service;
import com.spartans.model.Transaction;

import java.util.List;

public interface StudentService {
    public Student createStudent(RegisterRequestDTO request);
    public Student getStudent(Long id);
    public Transaction borrowBook(Long studentId, Long bookId);
    public Transaction returnBook(Long studentId, Long bookId);
    public List<Transaction> getBorrowedBooks(Long studentId);
    public List<Transaction> getBorrowHistory(Long studentId);
}
