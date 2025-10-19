package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.exception.BookNotFoundException;
import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookServiceImplTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookServiceImpl bookService;

  private Book book;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    book = new Book();
    book.setBookId(1L);
    book.setBookTitle("Effective Java");
    book.setBookAuthor("Joshua Bloch");
    book.setCategory("Programming");
    book.setIsbn("978-0134685991");
    book.setPrice(700.50);
    book.setTotalCopies(10);
    book.setAvailableCopies(10);
    book.setCreatedAt(LocalDateTime.now());
    book.setPublicationDate(LocalDate.now());
  }

  //  Test addBook success
  @Test
  void testAddBook_Success() {
    when(bookRepository.findByIsbnIgnoreCase(anyString())).thenReturn(Optional.empty());
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    Book saved = bookService.addBook(book);

    assertNotNull(saved);
    assertEquals("Effective Java", saved.getBookTitle());
    verify(bookRepository, times(1)).save(book);
  }

  //  Test addBook duplicate ISBN
  //  @Test
  //  void testAddBook_DuplicateISBN() {
  //    when(bookRepository.findByIsbnIgnoreCase(book.getIsbn())).thenReturn(Optional.of(book));
  //
  //      DuplicateBookException exception =
  //        assertThrows(DuplicateBookException.class,
  //            () -> {
  //              bookService.addBook(book);
  //            });
  //
  //    assertEquals("Book with the same ISBN already exists", exception.getMessage());
  //  }

  //  Test getBookById success
  @Test
  void testGetBookById_Success() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    Book found = bookService.getBookById(1L);

    assertEquals("Effective Java", found.getBookTitle());
    verify(bookRepository, times(1)).findById(1L);
  }

  // Test getBookById not found
  @Test
  void testGetBookById_NotFound() {
    when(bookRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.getBookById(2L));
  }

  //  Test updateBook success
  @Test
  void testUpdateBook_Success() {
    Book updated = new Book();
    updated.setBookTitle("Clean Code");
    updated.setBookAuthor("Robert C. Martin");

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(any(Book.class))).thenReturn(updated);

    Book result = bookService.updateBook(1L, updated);

    assertEquals("Clean Code", result.getBookTitle());
    verify(bookRepository, times(1)).save(book);
  }

  //  Test updateBook not found
  @Test
  void testUpdateBook_NotFound() {
    when(bookRepository.findById(10L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.updateBook(10L, book));
  }

  //  Test deleteBook success
  @Test
  void testDeleteBook_Success() {
    when(bookRepository.existsById(1L)).thenReturn(true);
    doNothing().when(bookRepository).deleteById(1L);

    bookService.deleteBook(1L);
    verify(bookRepository, times(1)).deleteById(1L);
  }

  //  Test deleteBook not found
  @Test
  void testDeleteBook_NotFound() {
    when(bookRepository.existsById(99L)).thenReturn(false);

    assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(99L));
  }

  //  Test getAllBooks
  @Test
  void testGetAllBooks() {
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<Book> books = bookService.getAllBooks();

    assertEquals(1, books.size());
    verify(bookRepository, times(1)).findAll();
  }

  //  Test getBookDetails success
  @Test
  void testGetBookDetails_Success() {
    when(bookRepository.findByBookTitleIgnoreCase("Effective Java")).thenReturn(Optional.of(book));

    Book result = bookService.getBookDetails("Effective Java");

    assertEquals("Effective Java", result.getBookTitle());
    verify(bookRepository, times(1)).findByBookTitleIgnoreCase("Effective Java");
  }

  //  Test getBookDetails not found
  @Test
  void testGetBookDetails_NotFound() {
    when(bookRepository.findByBookTitleIgnoreCase("Unknown Book")).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.getBookDetails("Unknown Book"));
  }
}
