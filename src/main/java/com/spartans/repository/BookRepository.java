package com.spartans.repository;

import com.spartans.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

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

}

