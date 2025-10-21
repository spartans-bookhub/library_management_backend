package com.spartans.service;

import com.spartans.dto.BookDTO;
import java.util.List;
import java.util.Map;

public interface BookService {

  List<BookDTO> getAllBooks();

  BookDTO addBook(BookDTO book);

  BookDTO getBookById(Long id);

  BookDTO getBookDetails(String bookTitle);

  BookDTO updateBook(Long id, BookDTO updatedBook);

  void deleteBook(Long id);

  Map<String, Object> searchBook(String keyword);
}
