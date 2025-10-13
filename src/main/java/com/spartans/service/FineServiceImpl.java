package com.spartans.service;

import com.spartans.model.Fine;
import com.spartans.model.Transaction;
import com.spartans.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FineServiceImpl implements FineService {

    private static final double DAILY_FINE = 10.0; // example

    @Autowired
    private FineRepository fineRepository;

    @Override
    public Fine calculateFine(Transaction transaction) {
        if (transaction.getReturnDate() == null && transaction.getDueDate().isBefore(LocalDate.now())) {
            long daysOverdue = Duration.between(transaction.getDueDate(), LocalDateTime.now()).toDays();
            double amount = daysOverdue * DAILY_FINE;

            Fine fine = new Fine();
            fine.setTransaction(transaction);
            fine.setAmount(amount);
            fine.setPaid(false);

            return fineRepository.save(fine);
        }
        return null;
    }

    @Override
    public List<Fine> getOverdueFines(Long studentId) {
        return fineRepository.findByTransactionStudentIdAndPaidFalse(studentId);
    }
}
