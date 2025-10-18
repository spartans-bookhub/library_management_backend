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

class TransactionServiceImplTest {

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
  void borrowBook_successful() {
    Long userId = 1L;
    Long bookId = 1L;

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

    Transaction transaction = new Transaction();
    transaction.setUser(user);
    transaction.setBook(book);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.borrowBook(userId, bookId);

    assertNotNull(result);
    assertEquals(user, result.getUser());
    assertEquals(book, result.getBook());
    verify(notificationService).sendBookBorrowedNotification(user, book);
    verify(bookRepository).save(book);
  }

  @Test
  void borrowBook_userNotFound_throwsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    ResourceNotFoundException ex =
        assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 1L));
    assertTrue(ex.getMessage().contains("User not found"));
  }

  @Test
  void borrowBook_bookNotAvailable_throwsException() {
    Long userId = 1L;
    Long bookId = 1L;

    User user = new User();
    user.setUserId(userId);
    user.setUserRole("STUDENT");

    Book book = new Book();
    book.setBookId(bookId);
    book.setAvailableCopies(0);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRoleConfig.getStudent()).thenReturn("STUDENT");
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    BookNotAvailableException ex =
        assertThrows(
            BookNotAvailableException.class, () -> transactionService.borrowBook(userId, bookId));
    assertTrue(ex.getMessage().contains("Book is not available"));
  }

  // ------------------- returnBook Tests -------------------
  @Test
  void returnBook_successful_noFine() {
    Long userId = 1L;
    Long bookId = 1L;

    User user = new User();
    user.setUserId(userId);

    Book book = new Book();
    book.setBookId(bookId);
    book.setAvailableCopies(2);

    Transaction transaction = new Transaction();
    transaction.setBook(book);
    transaction.setUser(user);
    transaction.setDueDate(LocalDate.now().plusDays(1));
    transaction.setTransactionStatus("BORROWED");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(transaction));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.returnBook(userId, bookId);

    assertNotNull(result);
    assertEquals("BORROWED".equals(result.getTransactionStatus()), false);
    verify(notificationService).sendBookReturnedNotification(user, book);
    assertEquals(3, book.getAvailableCopies());
  }

  @Test
  void returnBook_lateReturn_calculatesFine() {
    Long userId = 1L;
    Long bookId = 1L;

    User user = new User();
    user.setUserId(userId);

    Book book = new Book();
    book.setBookId(bookId);
    book.setAvailableCopies(1);

    Transaction transaction = new Transaction();
    transaction.setBook(book);
    transaction.setUser(user);
    transaction.setDueDate(LocalDate.now().minusDays(3));
    transaction.setTransactionStatus("BORROWED");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(transaction));
    when(libraryConfig.getDailyFineRate()).thenReturn(10.0);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.returnBook(userId, bookId);

    assertTrue(result.getFineAmount() > 0);
    verify(notificationService).sendLateReturnNotification(user, book, 3L, 30.0);
    verify(bookRepository).save(book);
  }

  // ------------------- canBorrowMoreBooks Tests -------------------
  @Test
  void canBorrowMoreBooks_true() {
    User user = new User();
    user.setUserId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(1L);
    when(libraryConfig.getMaxBorrowLimit()).thenReturn(3);

    boolean result = transactionService.canBorrowMoreBooks(1L);
    assertTrue(result);
  }

  @Test
  void canBorrowMoreBooks_false() {
    User user = new User();
    user.setUserId(1L);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(3L);
    when(libraryConfig.getMaxBorrowLimit()).thenReturn(3);

    boolean result = transactionService.canBorrowMoreBooks(1L);
    assertFalse(result);
  }
}
