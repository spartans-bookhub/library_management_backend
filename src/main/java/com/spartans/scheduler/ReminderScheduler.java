package com.spartans.scheduler;

import com.spartans.model.Transaction;
import com.spartans.repository.TransactionRepository;
import com.spartans.service.NotificationService;
import com.spartans.util.ReminderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    // Run every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyReminders() {
        List<Transaction> borrowedTransactions = transactionRepository.findByTransactionStatus("BORROWED");

        for (Transaction transaction : borrowedTransactions) {
            ReminderUtils.sendDueDateReminder(transaction, notificationService);
        }
    }
}