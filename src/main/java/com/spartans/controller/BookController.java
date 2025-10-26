package com.spartans.controller;

import com.spartans.dto.BookDTO;
import com.spartans.service.BookService;
import com.spartans.util.UserContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/books")
public class BookController {

  @Autowired private BookService bookService;

  // Add new book
  @PostMapping
  public ResponseEntity<?> addBook(@RequestBody BookDTO book) {
    UserContext.checkAdmin();
    BookDTO savedBook = bookService.addBook(book);
    return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
  }

  // Get all books
  @GetMapping("/list")
  public ResponseEntity<?> getAllBooks() {
    try {
      List<BookDTO> books = bookService.getAllBooks();
      return ResponseEntity.ok(books);
    } catch (RuntimeException e) {
      // If list empty
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Collections.singletonMap("message", e.getMessage()));
    } catch (Exception e) {
      // Any unexpected error
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", "Something went wrong"));
    }
  }

  // Get book by ID
  @GetMapping("/{id}")
  public ResponseEntity<?> getBookById(@PathVariable Long id) {
    BookDTO book = bookService.getBookById(id);
    return new ResponseEntity<>(book, HttpStatus.OK);
  }

  // Update existing book
  @PutMapping("/{id}")
  public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDTO updatedBook) {
    UserContext.checkAdmin();
    BookDTO updated = bookService.updateBook(id, updatedBook);
    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBook(@PathVariable Long id) {
    UserContext.checkAdmin();
    bookService.deleteBook(id);
    return ResponseEntity.ok("Book deleted successfully");
  }

  // Search Books
  @GetMapping("/search")
  public ResponseEntity<Map<String, Object>> searchBook(@RequestParam String keyword) {
    Map<String, Object> result = bookService.searchBook(keyword);
    return ResponseEntity.ok(result);
  }
}
