package com.spartans.scheduler;

import com.spartans.model.Transaction;
import com.spartans.repository.TransactionRepository;
import com.spartans.service.NotificationService;
import com.spartans.util.ReminderUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

  @Autowired private TransactionRepository transactionRepository;

  @Autowired private NotificationService notificationService;

  // Run every day at 9 AM
  @Scheduled(cron = "0 0 9 * * ?")
  public void sendDailyReminders() {
    List<Transaction> borrowedTransactions =
        transactionRepository.findByTransactionStatus("BORROWED");

      for (Transaction transaction : borrowedTransactions) {
          try {
              ReminderUtils.sendDueDateReminder(transaction, notificationService);
          } catch (Exception e) {
              System.err.println("Failed to send reminder for transaction " + transaction.getTransactionId() + ": " + e.getMessage());
          }
      }
  }
}
