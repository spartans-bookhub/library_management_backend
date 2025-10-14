package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spartans.exception.DuplicateBookException;
import com.spartans.exception.BookNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    // Get all books
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Add new book to bookList
    @Override
    public Book addBook(Book book) {
        // check if book with same title/isbn alraedy exist
        if (bookRepository.findByIsbnIgnoreCase(book.getIsbn()).isPresent()) {
            throw new DuplicateBookException("Book with title '" + book.getBookTitle() + "' already exists!");
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
        existingBook.setAvailabilityStatus(updatedBook.getAvailabilityStatus());

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
//    public Book getBookId(Long id)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    @Override
    public Book getBookDetails(String bookTitle) {
//        return bookRepository.findByBookTitleIgnoreCase(bookTitle)
//                .orElseThrow(() -> new BookNotFoundException("Book not found with title: " + bookTitle));

        Optional<Book> optionalBook = bookRepository.findByBookTitleIgnoreCase(bookTitle);
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        } else {
            throw new BookNotFoundException("Book not found with title: " + bookTitle);
        }
    }
    // Get book from repository
//        Book book = bookRepository.findByBookTitleIgnoreCase(bookTitle);
//
//        if (book != null) {
//            response.put("Title", book.getBookTitle());
//            response.put("Author", book.getBookAuthor());
//            response.put("Category", book.getCategory());

//            if (book.getAvailabilityStatus().equalsIgnoreCase("YES")) {
//                response.put("Availability", "Available");
//            } else {  response.put("Availability", "Not Available");  }
//        } else { response.put("Message", "No book found with title: " + bookTitle); }
//        return response;
//    }
}
