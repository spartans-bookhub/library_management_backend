package com.spartans.service;

import com.spartans.dto.BookDTO;
import com.spartans.exception.BookNotFoundException;
import com.spartans.mapper.BookMapper;
import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

  @Autowired private BookRepository bookRepository;

  @Autowired private BookMapper mapper;

  @Override
  public List<BookDTO> getAllBooks() {
    List<Book> books = bookRepository.findAll();

    if (books.isEmpty()) {
      throw new RuntimeException("No books are available");
    }

    return books.stream().map((book) -> mapper.toBookDto(book)).collect(Collectors.toList());
  }

  // Searchbook
  @Override
  public Map<String, Object> searchBook(String keyword) {
    Map<String, Object> response = new HashMap<>();

    try {

      String normalizedKeyword = keyword.replaceAll("\\s+", "").toLowerCase();

      List<Book> books = bookRepository.findByBookTitle(keyword);

      if (books.isEmpty()) {
        books = bookRepository.findByBookAuthor(keyword);
      }
      if (books.isEmpty()) {
        books = bookRepository.findByIsbn(keyword);
      }
      if (books.isEmpty()) {
        books = bookRepository.findByCategory(keyword);
      }

      if (books.isEmpty()) {
        List<Book> allBooks = bookRepository.findAll();
        for (Book book : allBooks) {
          String normalizedTitle = book.getBookTitle().replaceAll("\\s+", "").toLowerCase();
          String normalizedAuthor = book.getBookAuthor().replaceAll("\\s+", "").toLowerCase();
          String normalizedIsbn = book.getIsbn().replaceAll("\\s+", "").toLowerCase();
          String normalizedCategory = book.getCategory().replaceAll("\\s+", "").toLowerCase();

          if (normalizedTitle.contains(normalizedKeyword)
              || normalizedAuthor.contains(normalizedKeyword)
              || normalizedIsbn.contains(normalizedKeyword)
              || normalizedCategory.contains(normalizedKeyword)) {
            books.add(book);
          }
        }
      }

      if (!books.isEmpty()) {
        List<Map<String, Object>> bookList = new ArrayList<>();
        for (Book book : books) {
          Map<String, Object> result = new HashMap<>();
          result.put("Title", book.getBookTitle());
          result.put("Author", book.getBookAuthor());
          result.put("Category", book.getCategory());
          result.put("ISBN", book.getIsbn());
          result.put("Image URL", book.getImageUrl());
          result.put("Publisher Name", book.getPublisherName());
          result.put("Publication Date", book.getPublicationDate());
          result.put("Availability", book.getAvailableCopies() > 0 ? "Available" : "Not Available");
          bookList.add(result);
        }
        response.put("books", bookList);
      } else {
        response.put("message", "Book not found for keyword: " + keyword);
      }

    } catch (Exception e) {
      response.put("Error", "An unexpected error occurred: " + e.getMessage());
    }

    return response;
  }

  // Add new book to bookList
  @Override
  public BookDTO addBook(BookDTO bookDto) {
    // check if book with same title/isbn alraedy exist
    Book book =
        bookRepository
            .findByIsbnIgnoreCase(bookDto.isbn())
            .map(
                (existingBook) -> {
                  existingBook.setTotalCopies(existingBook.getTotalCopies() + 1);
                  existingBook.setAvailableCopies(existingBook.getAvailableCopies() + 1);
                  return existingBook;
                })
            .orElseGet(
                () -> {
                  Book newBook = mapper.toBookEntity(bookDto);
                  newBook.setCreatedAt(LocalDateTime.now());
                  newBook.setAvailableCopies(1);
                  newBook.setTotalCopies(1);
                  return newBook;
                });
    Book savedBook = bookRepository.save(book);
    return mapper.toBookDto(savedBook);
  }

  // Update book
  @Override
  public BookDTO updateBook(Long id, BookDTO updatedBookDto) {
    Book book = mapper.toBookEntity(updatedBookDto);
    Book existingBook =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book is not found with id: " + id));
    existingBook.setBookTitle(book.getBookTitle());
    existingBook.setBookAuthor(book.getBookAuthor());
    existingBook.setCategory(book.getCategory());
    existingBook.setIsbn(book.getIsbn());
    existingBook.setImageUrl(book.getImageUrl());
    existingBook.setPublisherName(book.getPublisherName());
    existingBook.setPublicationDate(book.getPublicationDate());
    existingBook.setPrice(book.getPrice());
    existingBook.setTotalCopies(book.getTotalCopies());
    existingBook.setAvailableCopies(book.getAvailableCopies());
    return mapper.toBookDto(bookRepository.save(existingBook));
  }

  public void deleteBook(Long id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException("Book not found with id: " + id);
    }
    bookRepository.deleteById(id);
  }

  // Get book by ID
  @Override
  public BookDTO getBookById(Long id) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    return mapper.toBookDto(book);
  }

  @Override
  public BookDTO getBookDetails(String bookTitle) {
    Optional<Book> optionalBook = bookRepository.findByBookTitleIgnoreCase(bookTitle);
    if (optionalBook.isPresent()) {
      return mapper.toBookDto(optionalBook.get());
    } else {
      throw new BookNotFoundException("Book not found with title: " + bookTitle);
    }
  }
}
