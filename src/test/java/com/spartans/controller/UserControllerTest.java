package com.spartans.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    private Transaction transaction;
    private Book book;
    private User user;  // ✅ Added mock user

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        objectMapper = new ObjectMapper();


        user = new User();
        user.setUserId(10L);
        user.setUserName("John Doe");
        user.setUserEmail("john@example.com");
        user.setUserRole("STUDENT");


        book = new Book();
        book.setBookId(100L);
        book.setBookTitle("Test Book");

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setBorrowDate(LocalDate.now());
    }

    @Test
    void testBorrowBook() throws Exception {
        when(transactionService.borrowBook(anyLong(), anyLong())).thenReturn(transaction);

        mockMvc.perform(post("/api/students/10/borrow/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1L))
                .andExpect(jsonPath("$.bookId").value(100L))
                .andExpect(jsonPath("$.userId").value(10L));
    }

    @Test
    void testReturnBook() throws Exception {
        when(transactionService.returnBook(anyLong(), anyLong())).thenReturn(transaction);

        mockMvc.perform(post("/api/students/10/return/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1L));
    }

    @Test
    void testGetBorrowedBooks() throws Exception {
        when(transactionService.getBorrowedBooks(anyLong())).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/students/10/borrowed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value(1L))
                .andExpect(jsonPath("$[0].bookId").value(100L));
    }

    @Test
    void testGetOverdueBooks() throws Exception {
        when(transactionService.getOverdueBooks(anyLong())).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/students/10/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10L));
    }

    @Test
    void testGetBorrowingHistory() throws Exception {
        when(transactionService.getBorrowingHistory(anyLong())).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/students/10/borrowing-history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(100L));
    }

    @Test
    void testCanBorrowMoreBooks() throws Exception {
        when(transactionService.canBorrowMoreBooks(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/students/10/can-borrow"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testGetAvailableBooks() throws Exception {
        when(transactionService.getAvailableBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/students/books/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookName").value("Test Book"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void testIsBookAvailable() throws Exception {
        when(transactionService.isBookAvailable(anyLong())).thenReturn(true);

        mockMvc.perform(get("/api/students/books/100/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
