package com.spartans.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.spartans.config.*;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.model.*;
import com.spartans.repository.*;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
@Rollback
public class TransactionServiceIntegrationTest {

  @Autowired private TransactionServiceImpl transactionService;

  @Autowired private UserRepository userRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private AuthRepository userAuthRepository;

  @Autowired private TransactionRepository transactionRepository;

  @Autowired private LibraryConfig libraryConfig;

  @Autowired private TransactionStatusConfig transactionStatusConfig;

  @Autowired private UserRoleConfig userRoleConfig;

  private User testUser;
  private Book testBook;
  private UserAuth testUserAuth;

  @BeforeEach
  void setUp() {
    // Create and persist UserAuth first
    testUserAuth = new UserAuth();
    testUserAuth.setEmail("nishu@example.com");
    testUserAuth.setPassword("pass123");
    testUserAuth.setRole(userRoleConfig.getStudent());
    userAuthRepository.save(testUserAuth);

    // Create User
    testUser = new User();
    testUser.setUserName("Nishu");
    testUser.setCreatedAt(LocalDateTime.now());
    testUser.setContactNumber("9876543210");
    testUser.setAddress("Delhi");
    testUser.setUserAuth(testUserAuth);
    userRepository.save(testUser);

    // Create Book
    testBook = new Book();
    testBook.setBookTitle("Spring Boot");
    testBook.setAvailableCopies(3);
    testBook.setTotalCopies(3);
    bookRepository.save(testBook);
  }

  @Test
  void testBorrowBook() {
    Transaction transaction =
        transactionService.borrowBook(testUser.getUserId(), testBook.getBookId());

    assertThat(transaction.getTransactionId()).isNotNull();
    assertThat(transaction.getUser().getUserName()).isEqualTo("Nishu");
    assertThat(transaction.getBook().getBookTitle()).isEqualTo("Spring Boot");
    assertThat(transaction.getTransactionStatus()).isEqualTo(transactionStatusConfig.getBorrowed());
    assertThat(transaction.getBook().getAvailableCopies()).isEqualTo(2);
  }

  @Test
  void testReturnBook() {
    Transaction borrowed =
        transactionService.borrowBook(testUser.getUserId(), testBook.getBookId());
    BorrowedBookDTO returned =
        transactionService.returnBook(testUser.getUserId(), testBook.getBookId());

    assertThat(returned.getBookId()).isEqualTo(testBook.getBookId());
    assertThat(returned.getReturnDate()).isNotNull();
    assertThat(returned.getFineAmount()).isGreaterThanOrEqualTo(0.0);
    assertThat(borrowed.getBook().getAvailableCopies()).isGreaterThan(0);
  }

  @Test
  void testGetBorrowedBooks() {
    transactionService.borrowBook(testUser.getUserId(), testBook.getBookId());
    List<Transaction> borrowedBooks = transactionService.getBorrowedBooks(testUser.getUserId());

    assertThat(borrowedBooks).hasSize(1);
    assertThat(borrowedBooks.get(0).getTransactionStatus())
        .isEqualTo(transactionStatusConfig.getBorrowed());
  }

  @Test
  void testGetOverdueBooks() {
    // create a borrowed transaction manually
    Transaction transaction = new Transaction();
    transaction.setUser(testUser);
    transaction.setBook(testBook);
    transaction.setBorrowDate(LocalDate.now().minusDays(10));
    transaction.setDueDate(LocalDate.now().minusDays(5));
    transaction.setTransactionStatus(transactionStatusConfig.getBorrowed());
    transactionRepository.save(transaction);

    List<Transaction> overdueBooks = transactionService.getOverdueBooks(testUser.getUserId());

    assertThat(overdueBooks).hasSize(1);
    assertThat(overdueBooks.get(0).getBook().getBookTitle()).isEqualTo("Spring Boot");
  }

  @Test
  void testCanBorrowMoreBooks() {
    boolean canBorrow = transactionService.canBorrowMoreBooks(testUser.getUserId());
    assertThat(canBorrow).isTrue();

    // Borrow books up to limit
    for (int i = 0; i < libraryConfig.getMaxBorrowLimit(); i++) {
      Book book = new Book();
      book.setBookTitle("Book " + i);
      book.setAvailableCopies(1);
      book.setTotalCopies(1);
      bookRepository.save(book);
      transactionService.borrowBook(testUser.getUserId(), book.getBookId());
    }

    boolean canBorrowAfterLimit = transactionService.canBorrowMoreBooks(testUser.getUserId());
    assertThat(canBorrowAfterLimit).isFalse();
  }

  @Test
  void testUpdateBookInventory() {
    Book updated = transactionService.updateBookInventory(testBook.getBookId(), 5);
    assertThat(updated.getTotalCopies()).isEqualTo(8);
    assertThat(updated.getAvailableCopies()).isEqualTo(8);
  }
}
