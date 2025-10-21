package com.spartans.service;

import com.spartans.exception.BookNotFoundException;
import com.spartans.exception.DuplicateBookException;
import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

  @Autowired private BookRepository bookRepository;

  // Get all books
//  @Override
//  public List<Book> getAllBooks() {
//    return bookRepository.findAll();
//  }


    @Override
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            throw new RuntimeException("No books are available");
        }

        return books;
    }
    //Searchbook
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

                    if (normalizedTitle.contains(normalizedKeyword) ||
                            normalizedAuthor.contains(normalizedKeyword) ||
                            normalizedIsbn.contains(normalizedKeyword) ||
                            normalizedCategory.contains(normalizedKeyword)) {
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
                    result.put("Availability", book.isAvailable() ? "Available" : "Not Available");
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
  public Book addBook(Book book) {
    // check if book with same title/isbn alraedy exist
    if (bookRepository.findByIsbnIgnoreCase(book.getIsbn()).isPresent()) {
      throw new DuplicateBookException(
          "Book with title '" + book.getBookTitle() + "' already exists!");
    }
    book.setCreatedAt(LocalDateTime.now());
    return bookRepository.save(book);
  }

  // Update book
  @Override
  public Book updateBook(Long id, Book updatedBook) {
    Book existingBook = getBookById(id);

    existingBook.setBookTitle(updatedBook.getBookTitle());
    existingBook.setBookAuthor(updatedBook.getBookAuthor());
    existingBook.setCategory(updatedBook.getCategory());
    existingBook.setIsbn(updatedBook.getIsbn());
    existingBook.setImageUrl(updatedBook.getImageUrl());
    existingBook.setPublisherName(updatedBook.getPublisherName());
    existingBook.setPublicationDate(updatedBook.getPublicationDate());
    existingBook.setPrice(updatedBook.getPrice());
    existingBook.setTotalCopies(updatedBook.getTotalCopies());
    existingBook.setAvailableCopies(updatedBook.getAvailableCopies());
    return bookRepository.save(existingBook);
  }

  public void deleteBook(Long id) {
    if (!bookRepository.existsById(id)) {
      throw new BookNotFoundException("Book not found with id: " + id);
    }
    bookRepository.deleteById(id);
  }

  // Get book by ID
  @Override
  public Book getBookById(Long id) {
    return bookRepository
        .findById(id)
        .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
  }

  @Override
  public Book getBookDetails(String bookTitle) {
    Optional<Book> optionalBook = bookRepository.findByBookTitleIgnoreCase(bookTitle);
    if (optionalBook.isPresent()) {
      return optionalBook.get();
    } else {
      throw new BookNotFoundException("Book not found with title: " + bookTitle);
    }
  }


}

