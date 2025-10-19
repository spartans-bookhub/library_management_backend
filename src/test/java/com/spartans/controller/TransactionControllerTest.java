package com.spartans.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import com.spartans.util.JWTUtils;
import com.spartans.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

class TransactionControllerTest {

  @InjectMocks private TransactionController controller;

  @Mock private TransactionService transactionService;

  @Mock private JWTUtils jwtUtils;

  @Mock private HttpServletRequest request;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testBorrowBook() {
    Long userId = 1L;
    Long bookId = 10L;
    Transaction mockTransaction = new Transaction();
    mockTransaction.setTransactionId(100L);

    when(request.getHeader("Authorization")).thenReturn("Bearer dummyToken");
    when(UserContext.getUserId()).thenReturn(userId);
    when(transactionService.borrowBook(userId, bookId)).thenReturn(mockTransaction);

    ResponseEntity<Transaction> response = controller.borrowBook(bookId, request);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(100L, response.getBody().getTransactionId());
    verify(transactionService, times(1)).borrowBook(userId, bookId);
  }

  @Test
  void testReturnBook() {
    Long userId = 1L;
    Long bookId = 10L;
    Transaction mockTransaction = new Transaction();
    mockTransaction.setTransactionId(101L);

    when(request.getHeader("Authorization")).thenReturn("Bearer dummyToken");
    when(UserContext.getUserId()).thenReturn(userId);
    when(transactionService.returnBook(userId, bookId)).thenReturn(mockTransaction);

    ResponseEntity<Transaction> response = controller.returnBook(bookId, request);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals(101L, response.getBody().getTransactionId());
    verify(transactionService, times(1)).returnBook(userId, bookId);
  }

  @Test
  void testGetBorrowedBooks() {
    Long userId = 1L;
    Transaction t1 = new Transaction();
    t1.setTransactionId(1L);
    Transaction t2 = new Transaction();
    t2.setTransactionId(2L);

    when(request.getHeader("Authorization")).thenReturn("Bearer dummyToken");
    when(UserContext.getUserId()).thenReturn(userId);
    when(transactionService.getBorrowedBooks(userId)).thenReturn(List.of(t1, t2));

    ResponseEntity<List<Transaction>> response = controller.getBorrowedBooks(request);

    assertEquals(2, response.getBody().size());
    verify(transactionService, times(1)).getBorrowedBooks(userId);
  }

  @Test
  void testGetBorrowingHistory() {
    Long userId = 1L;
    Transaction t = new Transaction();
    t.setTransactionId(200L);

    when(request.getHeader("Authorization")).thenReturn("Bearer dummyToken");
    when(UserContext.getUserId()).thenReturn(userId);
    when(transactionService.getBorrowingHistory(userId)).thenReturn(List.of(t));

    ResponseEntity<List<Transaction>> response = controller.getBorrowingHistory(request);

    assertEquals(1, response.getBody().size());
    assertEquals(200L, response.getBody().get(0).getTransactionId());
    verify(transactionService, times(1)).getBorrowingHistory(userId);
  }

  @Test
  void testCanBorrowMoreBooks() {
    Long userId = 1L;

    when(request.getHeader("Authorization")).thenReturn("Bearer dummyToken");
    when(UserContext.getUserId()).thenReturn(userId);
    when(transactionService.canBorrowMoreBooks(userId)).thenReturn(true);

    ResponseEntity<Boolean> response = controller.canBorrowMoreBooks(request);

    assertTrue(response.getBody());
    verify(transactionService, times(1)).canBorrowMoreBooks(userId);
  }
}
