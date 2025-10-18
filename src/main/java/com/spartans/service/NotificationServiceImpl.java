package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public void sendBookBorrowedNotification(User user, Book book) {
        String msg = String.format("Book Borrowed: You successfully borrowed '%s'.", book.getBookTitle());
        sendEmail(user.getEmail(), "Library Borrow Confirmation", msg);
    }

    @Override
    public void sendBookReturnedNotification(User user, Book book) {
        String msg = String.format("Book Returned: You returned '%s'. Thank you!", book.getBookTitle());
        sendEmail(user.getEmail(), "Library Return Confirmation", msg);
    }

    @Override
    public void sendLateReturnNotification(User user, Book book, Long daysLate, double fine) {
        String msg = String.format(
                "Late Return: You returned '%s' %d days late. Fine: $%.2f",
                book.getBookTitle(), daysLate, fine
        );
        sendEmail(user.getEmail(), "Library Late Return Notice", msg);
    }

    @Override
    public void sendDueDateReminder(User user, Book book, Long daysLeft) {
        String msg;
        if (daysLeft == 0) {
            msg = String.format("Reminder: Your borrowed book '%s' is due TODAY. Please return it to avoid fines.", book.getBookTitle());
        } else {
            msg = String.format("Reminder: Your borrowed book '%s' is due in %d day(s). Please return it on time.", book.getBookTitle(), daysLeft);
        }
        sendEmail(user.getEmail(), "Library Due Date Reminder", msg);
    }
}