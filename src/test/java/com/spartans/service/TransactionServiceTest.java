package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.config.LibraryConfig;
import com.spartans.config.TransactionStatusConfig;
import com.spartans.config.UserRoleConfig;
import com.spartans.exception.*;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.repository.BookRepository;
import com.spartans.repository.TransactionRepository;
import com.spartans.repository.UserRepository;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class TransactionServiceTest {

    @InjectMocks private TransactionServiceImpl transactionService;

    @Mock private BookRepository bookRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private LibraryConfig libraryConfig;
    @Mock private UserRoleConfig userRoleConfig;
    @Mock private TransactionStatusConfig transactionStatusConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- borrowBook Tests -------------------
    @Test
    void borrowBookSuccessful() {
        Long userId = 1L, bookId = 1L;

        User user = new User();
        user.setUserId(userId);
        user.setUserRole("STUDENT");

        Book book = new Book();
        book.setBookId(bookId);
        book.setAvailableCopies(5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
        when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
                .thenReturn(Optional.empty());
        when(libraryConfig.getMaxBorrowLimit()).thenReturn(3);
        when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(1L);
        when(libraryConfig.getBorrowPeriodDays()).thenReturn(7);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.borrowBook(userId, bookId);

        assertNotNull(result);
        verify(notificationService).sendBookBorrowedNotification(user, book);
        verify(bookRepository).save(book);
    }

    @Test
    void borrowBookUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBookNonStudentUserThrowsException() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");

        assertThrows(InvalidOperationException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBookNotFoundThrowsException() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("STUDENT");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBookBookNotAvailableThrowsException() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("STUDENT");

        Book book = new Book();
        book.setBookId(1L);
        book.setAvailableCopies(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBookAlreadyBorrowedThrowsException() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("STUDENT");

        Book book = new Book();
        book.setBookId(1L);
        book.setAvailableCopies(5);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setTransactionStatus("BORROWED");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
        when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
                .thenReturn(Optional.of(existingTransaction));

        assertThrows(BookAlreadyBorrowedException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    @Test
    void borrowBookExceedsBorrowLimitThrowsException() {
        User user = new User();
        user.setUserId(1L);
        user.setUserRole("STUDENT");

        Book book = new Book();
        book.setBookId(1L);
        book.setAvailableCopies(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleConfig.getStudent()).thenReturn("STUDENT");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
        when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
                .thenReturn(Optional.empty());
        when(libraryConfig.getMaxBorrowLimit()).thenReturn(3);
        when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(3L);

        assertThrows(BorrowLimitExceededException.class, () -> transactionService.borrowBook(1L, 1L));
    }

    // ------------------- returnBook Tests--------------
    @Test
    void returnBookTransactionNotFoundThrowsException() {
        User user = new User();
        user.setUserId(1L);
        Book book = new Book();
        book.setBookId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
        when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.returnBook(1L, 1L));
    }

    @Test
    void returnBookUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.returnBook(1L, 1L));
    }

    @Test
    void returnBookNotFoundThrowsException() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.returnBook(1L, 1L));
    }


    @Test
    void getBorrowedBooksUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getBorrowedBooks(1L));
    }


    @Test
    void getOverdueBooksUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getOverdueBooks(1L));
    }


    @Test
    void getBorrowingHistoryUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.getBorrowingHistory(1L));
    }

    @Test
    void canBorrowMoreBooksUserNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.canBorrowMoreBooks(1L));
    }

    @Test
    void updateBookInventoryBookNotFoundThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateBookInventory(1L, 5));
    }

    @Test
    void updateBookAvailabilityBookNotFoundThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.updateBookAvailability(1L, "YES"));
    }
}