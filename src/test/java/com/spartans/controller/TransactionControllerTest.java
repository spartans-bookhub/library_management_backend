package com.spartans.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.BorrowBooksRequest;
import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
import com.spartans.exception.BorrowLimitExceededException;
import com.spartans.exception.DuplicateBookException;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.service.TransactionService;
import com.spartans.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

class TransactionControllerTest {

  @InjectMocks private TransactionController controller;
  @Mock private TransactionService transactionService;
  @Mock private HttpServletRequest request;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  private Transaction createMockTransaction(
      Long transactionId, Long bookId, String title, Long userId, String status) {
    Transaction t = new Transaction();
    Book b = new Book();
    b.setBookId(bookId);
    b.setBookTitle(title);
    User u = new User();
    u.setUserId(userId);
    t.setTransactionId(transactionId);
    t.setBook(b);
    t.setUser(u);
    t.setBorrowDate(LocalDate.now());
    t.setDueDate(LocalDate.now().plusDays(7));
    t.setTransactionStatus(status); // Set transactionStatus
    return t;
  }

  // Borrow single book
  @Test
  void testBorrowBook() {
    Long userId = 1L;
    Long bookId = 10L;

    Transaction t = createMockTransaction(100L, bookId, "Book Title", userId, "BORROWED");

    when(transactionService.borrowBook(userId, bookId)).thenReturn(t);

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<BorrowedBookDTO> response = controller.borrowBook(bookId, request);

      assertEquals(200, response.getStatusCodeValue());
      BorrowedBookDTO dto = response.getBody();
      assertNotNull(dto);
      assertEquals(100L, dto.getTransactionId());
      assertEquals("Book Title", dto.getBookTitle());
      assertEquals(bookId, dto.getBookId());
      assertEquals(userId, dto.getUserId());
      assertEquals("BORROWED", dto.getTransactionStatus());
    }

    verify(transactionService).borrowBook(userId, bookId);
  }

  // Borrow multiple books
  @Test
  void testBorrowMultipleBooks() {
    Long userId = 1L;

    BorrowBooksRequest req = new BorrowBooksRequest();
    req.setBookIds(List.of(1L, 2L));

    BorrowedBookDTO dto1 =
        new BorrowedBookDTO(
            1L,
            1L,
            "Book A",
            userId,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            null,
            0.0,
            "BORROWED");
    BorrowedBookDTO dto2 =
        new BorrowedBookDTO(
            2L,
            2L,
            "Book B",
            userId,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            null,
            0.0,
            "BORROWED");

    BorrowBooksResponse mockResponse = new BorrowBooksResponse(List.of(dto1, dto2), Map.of());

    when(transactionService.borrowMultipleBooks(userId, req.getBookIds())).thenReturn(mockResponse);

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<BorrowBooksResponse> response = controller.borrowMultipleBooks(req, request);

      assertEquals(200, response.getStatusCodeValue());
      assertNotNull(response.getBody());
      assertEquals(2, response.getBody().getSuccess().size());
      assertEquals("BORROWED", response.getBody().getSuccess().get(0).getTransactionStatus());
      assertEquals("BORROWED", response.getBody().getSuccess().get(1).getTransactionStatus());
      assertTrue(response.getBody().getFailed().isEmpty());
    }

    verify(transactionService).borrowMultipleBooks(userId, req.getBookIds());
  }

  // Borrow multiple books with duplicate
  @Test
  void testBorrowMultipleBooks_withDuplicates() {
    Long userId = 1L;

    BorrowBooksRequest req = new BorrowBooksRequest();
    req.setBookIds(List.of(1L, 1L, 2L)); // duplicate book IDs

    when(transactionService.borrowMultipleBooks(userId, req.getBookIds()))
        .thenThrow(
            new DuplicateBookException(
                "Duplicate book entries found in request. Each book can be borrowed only once."));

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      DuplicateBookException ex =
          assertThrows(
              DuplicateBookException.class, () -> controller.borrowMultipleBooks(req, request));

      assertEquals(
          "Duplicate book entries found in request. Each book can be borrowed only once.",
          ex.getMessage());
      verify(transactionService).borrowMultipleBooks(userId, req.getBookIds());
    }
  }

  // Borrow multiple books exceeding limit
  @Test
  void testBorrowMultipleBooks_exceedsLimit() {
    Long userId = 1L;

    BorrowBooksRequest req = new BorrowBooksRequest();
    req.setBookIds(List.of(1L, 2L, 3L, 4L, 5L, 6L)); // exceeds limit

    when(transactionService.borrowMultipleBooks(userId, req.getBookIds()))
        .thenThrow(new BorrowLimitExceededException("Maximum borrow limit exceeded"));

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      BorrowLimitExceededException ex =
          assertThrows(
              BorrowLimitExceededException.class,
              () -> controller.borrowMultipleBooks(req, request));

      assertEquals("Maximum borrow limit exceeded", ex.getMessage());
      verify(transactionService).borrowMultipleBooks(userId, req.getBookIds());
    }
  }

  // Return book
  @Test
  void testReturnBook() {
    Long userId = 1L;
    Long bookId = 10L;

    BorrowedBookDTO returned =
        new BorrowedBookDTO(
            101L,
            bookId,
            "Returned Book",
            userId,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            LocalDate.now(),
            0.0,
            "RETURNED");

    when(transactionService.returnBook(userId, bookId)).thenReturn(returned);

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<BorrowedBookDTO> response = controller.returnBook(bookId, request);

      assertEquals(200, response.getStatusCodeValue());
      BorrowedBookDTO dto = response.getBody();
      assertNotNull(dto);
      assertEquals(101L, dto.getTransactionId());
      assertEquals("RETURNED", dto.getTransactionStatus());
      verify(transactionService).returnBook(userId, bookId);
    }
  }

  // Get borrowed books (BORROWED + OVERDUE)
  @Test
  void testGetBorrowedBooks() {
    Long userId = 1L;
    Transaction t1 = createMockTransaction(1L, 101L, "Alpha", userId, "BORROWED");
    Transaction t2 = createMockTransaction(2L, 102L, "Beta", userId, "DUE");

    when(transactionService.getBorrowedBooks(userId)).thenReturn(List.of(t1, t2));

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<List<BorrowedBookDTO>> response = controller.getBorrowedBooks(request);

      assertEquals(200, response.getStatusCodeValue());
      assertEquals(2, response.getBody().size());
      assertEquals("BORROWED", response.getBody().get(0).getTransactionStatus());
      assertEquals("DUE", response.getBody().get(1).getTransactionStatus());
      verify(transactionService).getBorrowedBooks(userId);
    }
  }

  // Get borrowing history
  @Test
  void testGetBorrowingHistory() {
    Long userId = 1L;
    Transaction t = createMockTransaction(200L, 99L, "History Book", userId, "RETURNED");

    when(transactionService.getBorrowingHistory(userId)).thenReturn(List.of(t));

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<List<BorrowedBookDTO>> response = controller.getBorrowingHistory(request);

      assertEquals(1, response.getBody().size());
      assertEquals(200L, response.getBody().get(0).getTransactionId());
      assertEquals("RETURNED", response.getBody().get(0).getTransactionStatus());
      verify(transactionService).getBorrowingHistory(userId);
    }
  }

  // Can borrow more books
  @Test
  void testCanBorrowMoreBooks() {
    Long userId = 1L;

    when(transactionService.canBorrowMoreBooks(userId)).thenReturn(true);

    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      ResponseEntity<Boolean> response = controller.canBorrowMoreBooks(request);

      assertTrue(response.getBody());
      verify(transactionService).canBorrowMoreBooks(userId);
    }
  }
}
