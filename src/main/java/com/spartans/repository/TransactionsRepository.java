package com.spartans.repository;

import com.spartans.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    // TODO: Add custom query methods
}
