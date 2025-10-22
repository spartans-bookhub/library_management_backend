package com.spartans.repository;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  // Find transactions by user and status
  List<Transaction> findByUserAndTransactionStatus(User user, String transactionStatus);

  // Find transactions by user
  List<Transaction> findByUser(User user);

  // Find active transaction for a specific user and book
  Optional<Transaction> findByUserAndBookAndTransactionStatus(
      User user, Book book, String transactionStatus);

  // Count active borrowings for a user
  long countByUserAndTransactionStatus(User user, String transactionStatus);

  // Find transactions by status
  List<Transaction> findByTransactionStatus(String transactionStatus);

  // Users with total fine above a threshold
  @Query(
      "SELECT t.user.id, t.user.userName, t.user.contactNumber, SUM(t.fineAmount) as totalFine "
          + "FROM Transaction t "
          + "GROUP BY t.user.id, t.user.userName, t.user.contactNumber "
          + "HAVING SUM(t.fineAmount) > :fineThreshold")
  List<Object[]> findUsersWithHighFines(@Param("fineThreshold") double fineThreshold);

  // Users with repeated late returns
  @Query(
      "SELECT t.user.id, t.user.userName, t.user.contactNumber, COUNT(t) as lateCount "
          + "FROM Transaction t "
          + "WHERE t.returnDate > t.dueDate "
          + "GROUP BY t.user.id, t.user.userName, t.user.contactNumber "
          + "HAVING COUNT(t) > :lateThreshold")
  List<Object[]> findUsersWithRepeatedLateReturns(@Param("lateThreshold") long lateThreshold);
}
