package com.spartans.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartans.exception.ResourceNotFoundException;
import com.spartans.model.Book;
import com.spartans.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setBookId(1L);
        book.setBookTitle("Effective Java");
        book.setBookAuthor("Joshua Bloch");
        book.setCategory("Programming");
        book.setIsbn("978-0134685991");
        book.setPrice(700.50);
        book.setPublicationDate(LocalDate.now());
    }

    //  Test addBook
    @Test
    void testAddBook() throws Exception {
        Mockito.when(bookService.addBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Effective Java"));
    }

    // Test getAllBooks
    @Test
    void testGetAllBooks() throws Exception {
        Mockito.when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Effective Java"));
    }

    //  Test getBookById
    @Test
    void testGetBookById() throws Exception {
        Mockito.when(bookService.getBookById(anyLong())).thenReturn(book);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Effective Java"));
    }

    //  Test getBookDetails by title
    @Test
    void testGetBookDetailsByTitle() throws Exception {
        Mockito.when(bookService.getBookDetails(anyString())).thenReturn(book);

        mockMvc.perform(get("/api/books/details")
                        .param("title", "Effective Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Effective Java"));
    }

    // Test updateBook
    @Test
    void testUpdateBook() throws Exception {
        Book updated = new Book();
        updated.setBookTitle("Clean Code");
        updated.setBookAuthor("Robert C. Martin");

        Mockito.when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(updated);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle").value("Clean Code"));
    }

    //  Test deleteBook
    @Test
    void testDeleteBook() throws Exception {
        Mockito.doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));
    }

    //  Test getBookById not found
    @Test
    void testGetBookById_NotFound() throws Exception {
        Mockito.when(bookService.getBookById(99L))
                .thenThrow(new ResourceNotFoundException("Book not found with id: 99"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found with id: 99"));
    }

    //  Test getBookDetails not found
    @Test
    void testGetBookDetails_NotFound() throws Exception {
        Mockito.when(bookService.getBookDetails("Unknown Book"))
                .thenThrow(new ResourceNotFoundException("Book not found with title: Unknown Book"));

        mockMvc.perform(get("/api/books/details")
                        .param("title", "Unknown Book"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found with title: Unknown Book"));
    }
}

