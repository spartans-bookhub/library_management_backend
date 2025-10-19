package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.User;

public interface NotificationService {
    void sendBookBorrowedNotification(User user, Book book);
    void sendBookReturnedNotification(User user, Book book);
    void sendLateReturnNotification(User user, Book book, Long daysLate, double fine);
    void sendDueDateReminder(User user, Book book, Long daysLeft);
}
