package com.spartans.service;

import com.spartans.model.Book;
import java.util.List;

public interface BookService {
    Book addBook(Book book);
    List<Book> getAllBooks();
    Book getBookById(Long id);
    Book getBookByTitle(String title);
    Book getBookTitle(String title);
    Book getBookDetails(String bookTitle);
    Book updateBook(Long id, Book updatedBook);
    void deleteBook(Long id);

}