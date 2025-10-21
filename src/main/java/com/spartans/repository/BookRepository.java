package com.spartans.repository;

import com.spartans.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  // Find books with available copies greater than specified number
  List<Book> findByAvailableCopiesGreaterThan(Integer availableCopies);

  // Find books with low available copies
  List<Book> findByAvailableCopiesLessThanEqual(Integer availableCopies);

  boolean existsByTitleIgnoreCase(String title);

  Optional<Book> findByTitleIgnoreCase(String bookTitle);

  Optional<Book> findByIsbnIgnoreCase(String isbn);

  Optional<Book> findByBookTitleIgnoreCase(String bookTitle);

    List<Book> findByBookTitle(String bookTitle);

    List<Book> findByBookAuthor(String bookAuthor);

    List<Book> findByIsbn(String isbn);

    List<Book> findByCategory(String category);
}
