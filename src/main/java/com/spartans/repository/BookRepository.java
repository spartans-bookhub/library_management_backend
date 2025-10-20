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

  // Check if a book with same title exists
  boolean existsByBookTitleIgnoreCase(String bookTitle);

  // Find book by bookTitle
  Optional<Book> findByBookTitleIgnoreCase(String bookTitle);

  // Find by ISBN
  Optional<Book> findByIsbnIgnoreCase(String isbn);
}
