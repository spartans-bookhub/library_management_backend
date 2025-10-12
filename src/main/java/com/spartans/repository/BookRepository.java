package com.spartans.repository;

import com.spartans.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find books by availability status
    List<Book> findByAvailabilityStatus(String availabilityStatus);
}
