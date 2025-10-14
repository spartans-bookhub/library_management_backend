package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendBookBorrowedNotification(User user, Book book) {
        // In a real implementation, this would send email, SMS, or push notification
        System.out.println("Notification: " + user.getUserName() + " has successfully borrowed '" + 
                          book.getBookTitle() + "' by " + book.getBookAuthor());
    }

    @Override
    public void sendBookReturnedNotification(User user, Book book) {
        // In a real implementation, this would send email, SMS, or push notification
        System.out.println("Notification: " + user.getUserName() + " has successfully returned '" + 
                          book.getBookTitle() + "' by " + book.getBookAuthor());
    }

    @Override
    public void sendLateReturnNotification(User user, Book book, long daysLate, double fine) {
        // In a real implementation, this would send email, SMS, or push notification
        System.out.println("Late Return Notification: " + user.getUserName() + " returned '" + 
                          book.getBookTitle() + "' " + daysLate + " days late. Fine: $" + fine);
    }

    @Override
    public void sendOutOfStockNotification(User user, Book book) {
        // In a real implementation, this would send email, SMS, or push notification
        System.out.println("Out of Stock Notification: " + user.getUserName() + " tried to borrow '" + 
                          book.getBookTitle() + "' but it's currently out of stock.");
    }
}
