package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> bookList = bookRepository.findAll();
        return bookList;
    }


    @Override
    public Map<String, Object> getBookDetails(String bookTitle) {
        Map<String, Object> response = new HashMap<>();

        // Get book from repository
        Book book = bookRepository.findByBookTitleIgnoreCase(bookTitle);

        if (book != null) {
            response.put("Title", book.getBookTitle());
            response.put("Author", book.getBookAuthor());
            response.put("Category", book.getCategory());

            // Check availability
            if (book.getAvailabilityStatus().equalsIgnoreCase("YES")) {
                response.put("Availability", "Available");
            } else {
                response.put("Availability", "Not Available");
            }
        } else {
            response.put("Message", "No book found with title: " + bookTitle);
        }

        return response;
    }

}
