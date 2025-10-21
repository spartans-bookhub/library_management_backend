package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.config.LibraryConfig;
import com.spartans.config.TransactionStatusConfig;
import com.spartans.config.UserRoleConfig;
import com.spartans.exception.*;
import com.spartans.model.*;
import com.spartans.repository.BookRepository;
import com.spartans.repository.TransactionRepository;
import com.spartans.repository.UserRepository;
import java.util.*;

import com.spartans.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class TransactionServiceImplTest {

  @InjectMocks private TransactionServiceImpl transactionService;

  @Mock private BookRepository bookRepository;
  @Mock private TransactionRepository transactionRepository;
  @Mock private UserRepository userRepository;
  @Mock private NotificationService notificationService;

  // Real config instances
  private LibraryConfig libraryConfig;
  private UserRoleConfig userRoleConfig;
  private TransactionStatusConfig transactionStatusConfig;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // LibraryConfig
    libraryConfig = new LibraryConfig();
    libraryConfig.setMaxBorrowLimit(3);
    libraryConfig.setBorrowPeriodDays(7);
    libraryConfig.setDailyFineRate(1.0);
    libraryConfig.setLowStockThreshold(5);

    // UserRoleConfig
    userRoleConfig = new UserRoleConfig();
    userRoleConfig.setStudent("STUDENT");
    userRoleConfig.setAdmin("ADMIN");

    // TransactionStatusConfig
    transactionStatusConfig = new TransactionStatusConfig();
    transactionStatusConfig.setBorrowed("BORROWED");
    transactionStatusConfig.setReturned("RETURNED");
    transactionStatusConfig.setDue("DUE");

    // Inject configs into service
    transactionService.setLibraryConfig(libraryConfig);
    transactionService.setUserRoleConfig(userRoleConfig);
    transactionService.setTransactionStatusConfig(transactionStatusConfig);
  }

  // ------------------- borrowBook Tests -------------------

  @Test
  void borrowBookSuccessful() {
    Long userId = 1L, bookId = 1L;

    User user = createUser(userId, "STUDENT");

    Book book = new Book();
    book.setBookId(bookId);
    book.setAvailableCopies(5);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.empty());
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(1L);
    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

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
    User user = createUser(1L, "ADMIN");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThrows(InvalidOperationException.class, () -> transactionService.borrowBook(1L, 1L));
  }

  @Test
  void borrowBookNotFoundThrowsException() {
    User user = createUser(1L, "STUDENT");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.borrowBook(1L, 1L));
  }

  @Test
  void borrowBookBookNotAvailableThrowsException() {
    User user = createUser(1L, "STUDENT");
    Book book = new Book();
    book.setBookId(1L);
    book.setAvailableCopies(0);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    assertThrows(BookNotAvailableException.class, () -> transactionService.borrowBook(1L, 1L));
  }

  @Test
  void borrowBookAlreadyBorrowedThrowsException() {
    User user = createUser(1L, "STUDENT");
    Book book = new Book();
    book.setBookId(1L);
    book.setAvailableCopies(5);

    Transaction existingTransaction = new Transaction();
    existingTransaction.setTransactionStatus("BORROWED");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.of(existingTransaction));

    assertThrows(BookAlreadyBorrowedException.class, () -> transactionService.borrowBook(1L, 1L));
  }

  @Test
  void borrowBookExceedsBorrowLimitThrowsException() {
    User user = createUser(1L, "STUDENT");
    Book book = new Book();
    book.setBookId(1L);
    book.setAvailableCopies(5);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(transactionRepository.findByUserAndBookAndTransactionStatus(user, book, "BORROWED"))
        .thenReturn(Optional.empty());
    when(transactionRepository.countByUserAndTransactionStatus(user, "BORROWED")).thenReturn(3L);

    assertThrows(BorrowLimitExceededException.class, () -> transactionService.borrowBook(1L, 1L));
  }

  // ------------------- returnBook Tests -------------------

  @Test
  void returnBookTransactionNotFoundThrowsException() {
    User user = createUser(1L, "STUDENT");
    Book book = new Book();
    book.setBookId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
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
    User user = createUser(1L, "STUDENT");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.returnBook(1L, 1L));
  }

  // ------------------- Utility Methods Tests -------------------

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
  void canBorrowMoreBooksUserNotFoundThrowsException() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> transactionService.canBorrowMoreBooks(1L));
  }

  @Test
  void updateBookInventoryBookNotFoundThrowsException() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class, () -> transactionService.updateBookInventory(1L, 5));
  }

  @Test
  void updateBookAvailabilityBookNotFoundThrowsException() {
    when(bookRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> transactionService.updateBookAvailability(1L, "YES"));
  }

    @Test
    void getBorrowingHistoryUserNotFoundThrowsException() {
        // Mock role as STUDENT
        Map<String, Object> mockUser =
                Map.of("id", 1L, "role", "STUDENT", "email", "test@gmail.com");
        UserContext.setUser(mockUser);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getBorrowingHistory(1L));

        UserContext.clear();
    }

    @Test
    void getBorrowingHistoryForAdminReturnsAllTransactions() {
        // Mock role as ADMIN
        Map<String, Object> mockAdmin =
                Map.of("id", 99L, "role", "ADMIN", "email", "admin@gmail.com");
        UserContext.setUser(mockAdmin);

        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getBorrowingHistory(99L);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findAll();
        verify(userRepository, never()).findById(anyLong());

        UserContext.clear();
    }

    @Test
    void getBorrowingHistoryForStudentReturnsOnlyTheirTransactions() {
        // Mock role as STUDENT
        Map<String, Object> mockStudent =
                Map.of("id", 10L, "role", "STUDENT", "email", "student@gmail.com");
        UserContext.setUser(mockStudent);

        User user = createUser(10L, "STUDENT");
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getBorrowingHistory(10L);

        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findByUser(user);
        verify(transactionRepository, never()).findAll();

        UserContext.clear();
    }

  // ------------------- Helper Method -------------------

  private User createUser(Long id, String role) {
    User user = new User();
    user.setUserId(id);
    UserAuth userAuth = new UserAuth();
    userAuth.setRole(role);
    user.setUserAuth(userAuth);
    return user;
  }
}
