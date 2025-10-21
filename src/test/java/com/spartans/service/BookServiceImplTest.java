package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.BookDTO;
import com.spartans.exception.BookNotFoundException;
import com.spartans.mapper.BookMapper;
import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookServiceImplTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookServiceImpl bookService;

  @Mock private BookMapper mapper;

  private Book book;
  private BookDTO bookDto;
  private Book savedBook;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    book =
        new Book(
            1L,
            "Effective Java",
            "Joshua Bloch",
            "Programming",
            "978-0134685991",
            "https://via.placeholder.com/150",
            "Publisher",
            LocalDate.now(),
            700.50,
            LocalDateTime.now(),
            50,
            50,
            5);
    bookDto =
        new BookDTO(
            1L,
            "Effective Java",
            "Joshua Bloch",
            "Programming",
            "978-0134685991",
            "https://via.placeholder.com/150",
            "Publisher",
            LocalDate.now(),
            700.50,
            LocalDateTime.now(),
            50,
            50,
            5);
  }

  //  Test addBook success
  @Test
  void testAddBook_Success() {

    when(mapper.toBookEntity(bookDto)).thenReturn(book);
    when(mapper.toBookDto(book)).thenReturn(bookDto);
    when(bookRepository.findByIsbnIgnoreCase(anyString())).thenReturn(Optional.empty());
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    BookDTO saved = bookService.addBook(bookDto);
    assertNotNull(saved);
    verify(bookRepository, times(1)).save(book);
  }

  //  Test getBookById success
  @Test
  void testGetBookById_Success() {
    when(mapper.toBookEntity(bookDto)).thenReturn(book);
    when(mapper.toBookDto(book)).thenReturn(bookDto);
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    BookDTO found = bookService.getBookById(1L);

    assertEquals("Effective Java", found.bookTitle());
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
    bookDto =
        new BookDTO(
            1L,
            "Clean Code",
            "Robert C. Martin",
            "Programming",
            "978-0134685991",
            "https://via.placeholder.com/150",
            "Publisher",
            LocalDate.now(),
            700.50,
            LocalDateTime.now(),
            50,
            50,
            5);
    when(mapper.toBookEntity(bookDto)).thenReturn(updated);
    when(mapper.toBookDto(updated)).thenReturn(bookDto);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
    when(bookRepository.save(any(Book.class))).thenReturn(updated);

    BookDTO result = bookService.updateBook(1L, bookDto);

    assertEquals("Clean Code", result.bookTitle());
    verify(bookRepository, times(1)).save(book);
  }

  //  Test updateBook not found
  @Test
  void testUpdateBook_NotFound() {

    when(mapper.toBookEntity(bookDto)).thenReturn(book);
    when(mapper.toBookDto(book)).thenReturn(bookDto);
    when(bookRepository.findById(10L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.updateBook(10L, bookDto));
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

    when(mapper.toBookDto(book)).thenReturn(bookDto);
    when(bookRepository.findAll()).thenReturn(List.of(book));

    List<BookDTO> books = bookService.getAllBooks();

    assertEquals(1, books.size());
    verify(bookRepository, times(1)).findAll();
  }

  //  Test getBookDetails success
  @Test
  void testGetBookDetails_Success() {
    when(mapper.toBookEntity(bookDto)).thenReturn(book);
    when(mapper.toBookDto(book)).thenReturn(bookDto);
    when(bookRepository.findByBookTitleIgnoreCase("Effective Java")).thenReturn(Optional.of(book));

    BookDTO result = bookService.getBookDetails("Effective Java");

    assertEquals("Effective Java", result.bookTitle());
    verify(bookRepository, times(1)).findByBookTitleIgnoreCase("Effective Java");
  }

  //  Test getBookDetails not found
  @Test
  void testGetBookDetails_NotFound() {
    when(bookRepository.findByBookTitleIgnoreCase("Unknown Book")).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.getBookDetails("Unknown Book"));
  }

  @Test
  void testSearchBookByTitle_Positive() {
    when(bookRepository.findByBookTitle("Java Basics")).thenReturn(Collections.singletonList(book));

    Map<String, Object> result = bookService.searchBook("Java Basics");

    assertTrue(result.containsKey("books"));
    List<Map<String, Object>> books = (List<Map<String, Object>>) result.get("books");
    assertEquals(1, books.size());
    assertEquals("Effective Java", books.get(0).get("Title"));
  }

  @Test
  void testSearchBookByAuthor_Positive() {
    when(bookRepository.findByBookTitle("John Doe")).thenReturn(Collections.emptyList());
    when(bookRepository.findByBookAuthor("John Doe")).thenReturn(Collections.singletonList(book));

    Map<String, Object> result = bookService.searchBook("John Doe");

    assertTrue(result.containsKey("books"));
  }

  @Test
  void testSearchBook_NotFound_Negative() {
    when(bookRepository.findByBookTitle("Unknown")).thenReturn(Collections.emptyList());
    when(bookRepository.findByBookAuthor("Unknown")).thenReturn(Collections.emptyList());
    when(bookRepository.findByIsbn("Unknown")).thenReturn(Collections.emptyList());
    when(bookRepository.findByCategory("Unknown")).thenReturn(Collections.emptyList());

    Map<String, Object> result = bookService.searchBook("Unknown");

    assertTrue(result.containsKey("message"));
    assertEquals("Book not found for keyword: Unknown", result.get("message"));
  }
}
