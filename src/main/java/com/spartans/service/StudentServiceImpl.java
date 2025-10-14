package com.spartans.service;
import com.spartans.exception.BorrowLimitExceededException;
import com.spartans.model.Book;
import com.spartans.model.Student;
import com.spartans.model.Transaction;
import com.spartans.repository.BookRepository;
import com.spartans.repository.StudentRepository;
import com.spartans.repository.TransactionRepository;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.exception.UserNotFoundException;
import com.spartans.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Service
public class StudentServiceImpl implements StudentService {

    private static final int BORROW_LIMIT = 5;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookInventoryService inventoryService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FineService fineService;
    
     @Autowired
    DTOMapper mapper;

    @Override
    public Student createStudent(RegisterRequestDTO request) {
        return studentRepository.save(mapper.toStudent(request));
    }

    @Override
    public Student getStudent(Long id){
        return studentRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Student not found"));
    }

    @Override
    public Transaction borrowBook(Long studentId, Long bookId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Transaction> activeTransactions = transactionRepository
                .findByStudentIdAndStatusIn(studentId, Arrays.asList("BORROWED", "OVERDUE"));

        if (activeTransactions.size() >= BORROW_LIMIT) {
            throw new BorrowLimitExceededException("Borrow limit exceeded");
        }

        if (!inventoryService.isAvailable(bookId)) {
            throw new RuntimeException("Book is out of stock");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        inventoryService.decrementStock(bookId);

        Transaction transaction = new Transaction();
        transaction.setStudent(student);
        transaction.setBook(book);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setDueDate(LocalDate.now().plusDays(14));
        transaction.setTransactionStatus("BORROWED");

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction returnBook(Long studentId, Long bookId) {
        Transaction transaction = transactionRepository
                .findByStudentIdAndBookIdAndStatus(studentId, bookId, "BORROWED")
                .orElseThrow(() -> new RuntimeException("Book already returned or not borrowed"));

        transaction.setReturnDate(LocalDate.now());

        if (transaction.getDueDate().isBefore(LocalDate.now())) {
            transaction.setTransactionStatus("OVERDUE");
            fineService.calculateFine(transaction);
        } else {
            transaction.setTransactionStatus("RETURNED");
        }

        inventoryService.incrementStock(bookId);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getBorrowedBooks(Long studentId) {
        return transactionRepository.findByStudentIdAndStatusIn(
                studentId, Arrays.asList("BORROWED", "OVERDUE"));
    }

    @Override
    public List<Transaction> getBorrowHistory(Long studentId) {
        return transactionRepository.findByStudentIdAndStatusIn(
                studentId, Arrays.asList("RETURNED", "OVERDUE", "BORROWED"));
   
}
