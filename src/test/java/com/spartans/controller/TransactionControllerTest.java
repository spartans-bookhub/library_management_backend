package com.spartans.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.BorrowBooksRequest;
import com.spartans.dto.BorrowBooksResponse;
import com.spartans.dto.BorrowedBookDTO;
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

    private Transaction createMockTransaction(Long transactionId, Long bookId, String title, Long userId) {
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
        return t;
    }

    // Borrow single book
    @Test
    void testBorrowBook() {
        Long userId = 1L;
        Long bookId = 10L;

        Transaction t = createMockTransaction(100L, bookId, "Book Title", userId);

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
        }

        verify(transactionService).borrowBook(userId, bookId);
    }

    // Borrow multiple books
    @Test
    void testBorrowMultipleBooks() {
        Long userId = 1L;

        BorrowBooksRequest req = new BorrowBooksRequest();
        req.setBookIds(List.of(1L, 2L));

        BorrowedBookDTO dto1 = new BorrowedBookDTO(1L, 1L, "Book A", userId, LocalDate.now(), LocalDate.now().plusDays(7), null, 0.0);
        BorrowedBookDTO dto2 = new BorrowedBookDTO(2L, 2L, "Book B", userId, LocalDate.now(), LocalDate.now().plusDays(7), null, 0.0);

        BorrowBooksResponse mockResponse = new BorrowBooksResponse(List.of(dto1, dto2), Map.of());

        when(transactionService.borrowMultipleBooks(userId, req.getBookIds())).thenReturn(mockResponse);

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

            ResponseEntity<BorrowBooksResponse> response = controller.borrowMultipleBooks(req, request);

            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getSuccess().size());
            assertTrue(response.getBody().getFailed().isEmpty());
        }

        verify(transactionService).borrowMultipleBooks(userId, req.getBookIds());
    }

    // Return book
    @Test
    void testReturnBook() {
        Long userId = 1L;
        Long bookId = 10L;

        BorrowedBookDTO returned = new BorrowedBookDTO(101L, bookId, "Returned Book", userId,
                LocalDate.now(), LocalDate.now().plusDays(7), LocalDate.now(), 0.0);

        when(transactionService.returnBook(userId, bookId)).thenReturn(returned);

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

            ResponseEntity<BorrowedBookDTO> response = controller.returnBook(bookId, request);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(101L, response.getBody().getTransactionId());
            verify(transactionService).returnBook(userId, bookId);
        }
    }

    // Get borrowed books
    @Test
    void testGetBorrowedBooks() {
        Long userId = 1L;
        Transaction t1 = createMockTransaction(1L, 101L, "Alpha", userId);
        Transaction t2 = createMockTransaction(2L, 102L, "Beta", userId);

        when(transactionService.getBorrowedBooks(userId)).thenReturn(List.of(t1, t2));

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

            ResponseEntity<List<BorrowedBookDTO>> response = controller.getBorrowedBooks(request);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
            verify(transactionService).getBorrowedBooks(userId);
        }
    }

    // Get borrowing history
    @Test
    void testGetBorrowingHistory() {
        Long userId = 1L;
        Transaction t = createMockTransaction(200L, 99L, "History Book", userId);

        when(transactionService.getBorrowingHistory(userId)).thenReturn(List.of(t));

        try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
            mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

            ResponseEntity<List<BorrowedBookDTO>> response = controller.getBorrowingHistory(request);

            assertEquals(1, response.getBody().size());
            assertEquals(200L, response.getBody().get(0).getTransactionId());
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