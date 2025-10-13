package com.spartans.repository;

import com.spartans.model.Transaction;
import com.spartans.service.StudentServiceImpl;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByStudentIdAndStatusIn(Long studentId, List<String> statuses);
    Optional<Transaction> findByStudentIdAndBookIdAndStatus(Long studentId, Long bookId, String status);
}