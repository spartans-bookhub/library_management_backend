package com.spartans.controller;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.service.TransactionService;
import com.spartans.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock UserContext to return USER_ID
        UserContext.setUserId(USER_ID);
    }

    @Test
    void borrowBook_ShouldReturnTransaction() {
        Transaction transaction = new Transaction();
        when(transactionService.borrowBook(USER_ID, 100L)).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.borrowBook(100L, null);

        assertEquals(transaction, response.getBody());
        verify(transactionService, times(1)).borrowBook(USER_ID, 100L);
    }

    @Test
    void returnBook_ShouldReturnTransaction() {
        Transaction transaction = new Transaction();
        when(transactionService.returnBook(USER_ID, 101L)).thenReturn(transaction);

        ResponseEntity<Transaction> response = transactionController.returnBook(101L, null);

        assertEquals(transaction, response.getBody());
        verify(transactionService, times(1)).returnBook(USER_ID, 101L);
    }

    @Test
    void getBorrowedBooks_ShouldReturnList() {
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionService.getBorrowedBooks(USER_ID)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getBorrowedBooks(null);

        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).getBorrowedBooks(USER_ID);
    }

    @Test
    void canBorrowMoreBooks_ShouldReturnBoolean() {
        when(transactionService.canBorrowMoreBooks(USER_ID)).thenReturn(true);

        ResponseEntity<Boolean> response = transactionController.canBorrowMoreBooks(null);

        assertEquals(true, response.getBody());
        verify(transactionService, times(1)).canBorrowMoreBooks(USER_ID);
    }

    @Test
    void getAvailableBooks_ShouldReturnList() {
        List<Book> books = Arrays.asList(new Book(), new Book());
        when(transactionService.getAvailableBooks()).thenReturn(books);

        ResponseEntity<List<Book>> response = transactionController.getAvailableBooks();

        assertEquals(books, response.getBody());
        verify(transactionService, times(1)).getAvailableBooks();
    }

    @Test
    void updateBookInventory_ShouldReturnBook() {
        Book book = new Book();
        when(transactionService.updateBookInventory(5L, 3)).thenReturn(book);

        ResponseEntity<Book> response = transactionController.updateBookInventory(5L, 3);

        assertEquals(book, response.getBody());
        verify(transactionService, times(1)).updateBookInventory(5L, 3);
    }

    @Test
    void isBookAvailable_ShouldReturnBoolean() {
        when(transactionService.isBookAvailable(10L)).thenReturn(true);

        ResponseEntity<Boolean> response = transactionController.isBookAvailable(10L);

        assertEquals(true, response.getBody());
        verify(transactionService, times(1)).isBookAvailable(10L);
    }
}