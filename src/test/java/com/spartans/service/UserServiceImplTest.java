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

public class UserServiceImplTest {

  @InjectMocks private TransactionServiceImpl transactionService;

  @Mock private BookRepository bookRepository;

  @Mock private TransactionRepository transactionRepository;

  @Mock private UserRepository userRepository;

  @Mock private NotificationService notificationService;

  @Mock private LibraryConfig libraryConfig;

  @Mock private UserRoleConfig userRoleConfig;

  @Mock private TransactionStatusConfig transactionStatusConfig;

  private User user;
  private Book book;
  private Transaction transaction;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setUserId(1L);
    user.setUserRole("STUDENT");

    book = new Book();
    book.setBookId(10L);
    book.setBookTitle("Java Basics");
    book.setAvailableCopies(2);

    transaction = new Transaction();
    transaction.setUser(user);
    transaction.setBook(book);
    transaction.setTransactionStatus("BORROWED");
    transaction.setDueDate(LocalDate.now().plusDays(7));

    when(libraryConfig.getMaxBorrowLimit()).thenReturn(5);
    when(libraryConfig.getBorrowPeriodDays()).thenReturn(7);
    when(libraryConfig.getDailyFineRate()).thenReturn(10.0);
    when(userRoleConfig.getStudent()).thenReturn("STUDENT");
    when(transactionStatusConfig.getBorrowed()).thenReturn("BORROWED");
    when(transactionStatusConfig.getReturned()).thenReturn("RETURNED");
  }

  // --- borrowBook tests ---

  @Test
  void borrowBook_Success() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.empty());
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(2L);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.borrowBook(1L, 10L);

    assertNotNull(result);
    verify(notificationService, times(1)).sendBookBorrowedNotification(user, book);
    verify(bookRepository, times(1)).save(book);
  }

  @Test
  void borrowBook_UserNotFound_ThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  @Test
  void borrowBook_BookNotFound_ThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  @Test
  void borrowBook_NotAStudent_ThrowsInvalidOperationException() {
    user.setUserRole("ADMIN");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThrows(InvalidOperationException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  @Test
  void borrowBook_BookNotAvailable_ThrowsException() {
    book.setAvailableCopies(0);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    assertThrows(BookNotAvailableException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  @Test
  void borrowBook_AlreadyBorrowed_ThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(transaction));
    assertThrows(BookAlreadyBorrowedException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  @Test
  void borrowBook_ExceedsLimit_ThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(5L);
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.empty());
    assertThrows(BorrowLimitExceededException.class, () -> transactionService.borrowBook(1L, 10L));
  }

  // --- returnBook tests ---

  @Test
  void returnBook_Success_NoFine() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(transaction));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.returnBook(1L, 10L);

    assertEquals("RETURNED", result.getTransactionStatus());
    verify(notificationService, times(1)).sendBookReturnedNotification(user, book);
  }

  @Test
  void returnBook_LateReturn_AddsFine() {
    transaction.setDueDate(LocalDate.now().minusDays(3)); // 3 days late
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(transaction));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    Transaction result = transactionService.returnBook(1L, 10L);

    assertTrue(result.getFineAmount() > 0);
    verify(notificationService, times(1))
        .sendLateReturnNotification(eq(user), eq(book), eq(3L), anyDouble());
  }

  @Test
  void returnBook_NoActiveBorrow_ThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.returnBook(1L, 10L));
  }

  // --- other methods ---

  @Test
  void getBorrowedBooks_ReturnsList() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(transactionRepository.findByUserAndTransactionStatus(user, "BORROWED"))
        .thenReturn(List.of(transaction));

    List<Transaction> result = transactionService.getBorrowedBooks(1L);
    assertEquals(1, result.size());
  }

  @Test
  void getOverdueBooks_ReturnsOnlyOverdue() {
    Transaction overdue = new Transaction();
    overdue.setUser(user);
    overdue.setBook(book);
    overdue.setDueDate(LocalDate.now().minusDays(2));
    overdue.setTransactionStatus("BORROWED");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(transactionRepository.findByUserAndTransactionStatus(user, "BORROWED"))
        .thenReturn(List.of(transaction, overdue));

    List<Transaction> result = transactionService.getOverdueBooks(1L);
    assertEquals(1, result.size());
  }

  @Test
  void canBorrowMoreBooks_ReturnsTrueIfBelowLimit() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(2L);
    assertTrue(transactionService.canBorrowMoreBooks(1L));
  }

  @Test
  void isBookAvailable_ReturnsTrue() {
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    assertTrue(transactionService.isBookAvailable(10L));
  }

  @Test
  void isBookAvailable_ReturnsFalseWhenOutOfStock() {
    book.setAvailableCopies(0);
    when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
    assertFalse(transactionService.isBookAvailable(10L));
  }
}
