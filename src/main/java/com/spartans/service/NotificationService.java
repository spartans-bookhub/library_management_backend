package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.User;

public interface NotificationService {
    
    // Send notification when book is borrowed
    void sendBookBorrowedNotification(User user, Book book);
    
    // Send notification when book is returned
    void sendBookReturnedNotification(User user, Book book);
    
    // Send notification for late return
    void sendLateReturnNotification(User user, Book book, long daysLate, double fine);
    
    // Send notification when book is out of stock
    void sendOutOfStockNotification(User user, Book book);
}
