package com.spartans.util;

import com.spartans.model.Book;
import com.spartans.model.Transaction;
import com.spartans.model.User;
import com.spartans.service.NotificationService;

import java.time.LocalDate;

public class ReminderUtils {

    private ReminderUtils() {} // private constructor to prevent instantiation

    public static void sendDueDateReminder(Transaction transaction, NotificationService notificationService) {
        User user = transaction.getUser();
        Book book = transaction.getBook();
        LocalDate today = LocalDate.now();

        if (transaction.getReturnDate() != null) return; // already returned

        long daysLeft = transaction.getDueDate().toEpochDay() - today.toEpochDay();
        if (daysLeft == 0 || daysLeft == 1) { // last day or 1 day before
            notificationService.sendDueDateReminder(user, book, daysLeft);
        }
    }
}