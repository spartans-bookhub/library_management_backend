package com.spartans.repository;

import com.spartans.model.BookInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookInventoryRepository extends JpaRepository<BookInventory, Long> {
    Optional<BookInventory> findByBookId(Long bookId);
}
