package com.spartans.service;

import com.spartans.exception.BorrowLimitExceededException;
import com.spartans.model.Book;
import com.spartans.model.Student;
import com.spartans.model.Transaction;
import com.spartans.repository.BookRepository;
import com.spartans.repository.StudentRepository;
import com.spartans.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
    public class StudentServiceImplTest {

        @Mock
        private StudentRepository studentRepository;

        @Mock
        private BookRepository bookRepository;

        @Mock
        private BookInventoryService inventoryService;

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private FineService fineService;

        @InjectMocks
        private StudentServiceImpl studentService;

        private Student student;
        private Book book;
        private Transaction transaction;

        @BeforeEach
        void setUp() {
            student = new Student();
            student.setStudentId(1L);
            student.setStudentName("Alice");

            book = new Book();
            book.setBookId(100L);
            book.setBookTitle("Java 101");

            transaction = new Transaction();
            transaction.setTransactionId(10L);
            transaction.setStudent(student);
            transaction.setBook(book);
            transaction.setTransactionStatus("BORROWED");
        }

        @Test
        void testBorrowBook_success() {
            Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
            Mockito.when(transactionRepository.findByStudentIdAndStatusIn(
                            Mockito.eq(1L),
                            Mockito.anyList()))
                    .thenReturn(new ArrayList<>());
            Mockito.when(inventoryService.isAvailable(100L)).thenReturn(true);
            Mockito.when(bookRepository.findById(100L)).thenReturn(Optional.of(book));
            Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);

            Transaction result = studentService.borrowBook(1L, 100L);

            Assertions.assertEquals("BORROWED", result.getTransactionStatus());
            Assertions.assertEquals(student, result.getStudent());
            Assertions.assertEquals(book, result.getBook());
            Mockito.verify(inventoryService).decrementStock(100L);
        }

        @Test
        void testBorrowBook_outOfStock() {
            Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
            Mockito.when(transactionRepository.findByStudentIdAndStatusIn(Mockito.eq(1L), Mockito.anyList()))
                    .thenReturn(new ArrayList<>());
            Mockito.when(inventoryService.isAvailable(100L)).thenReturn(false);

            Assertions.assertThrows(RuntimeException.class, () -> {
                studentService.borrowBook(1L, 100L);
            });
        }

        @Test
        void testBorrowBook_limitExceeded() {
            Mockito.when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
            List<Transaction> active = Arrays.asList(
                    new Transaction(), new Transaction(), new Transaction(), new Transaction(), new Transaction()
            );
            Mockito.when(transactionRepository.findByStudentIdAndStatusIn(Mockito.eq(1L), Mockito.anyList()))
                    .thenReturn(active);

            Assertions.assertThrows(BorrowLimitExceededException.class, () -> {
                studentService.borrowBook(1L, 100L);
            });
        }

        @Test
        void testReturnBook_success() {
            Mockito.when(transactionRepository.findByStudentIdAndBookIdAndStatus(1L, 100L, "BORROWED"))
                    .thenReturn(Optional.of(transaction));

            Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);

            Transaction returned = studentService.returnBook(1L, 100L);

            Assertions.assertTrue(returned.getTransactionStatus().equals("RETURNED") ||
                    returned.getTransactionStatus().equals("OVERDUE"));
            Mockito.verify(inventoryService).incrementStock(100L);
        }
}
