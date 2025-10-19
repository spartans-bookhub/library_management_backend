package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired private JavaMailSender mailSender;

  private void sendEmail(String to, String subject, String text) {
    if (to == null || to.isBlank()) {
      System.out.println("Cannot send email: recipient is null or empty.");
      return;
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    try {
      mailSender.send(message);
      System.out.println("Email sent to " + to + ": " + subject);
    } catch (Exception e) {
      System.err.println("Failed to send email to " + to + ": " + e.getMessage());
    }
  }

  @Async
  @Override
  public void sendBookBorrowedNotification(User user, Book book) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg =
        String.format("Book Borrowed: You successfully borrowed '%s'.", book.getBookTitle());
    sendEmail(email, "Library Borrow Confirmation", msg);
  }

  @Async
  @Override
  public void sendBookReturnedNotification(User user, Book book) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg = String.format("Book Returned: You returned '%s'. Thank you!", book.getBookTitle());
    sendEmail(email, "Library Return Confirmation", msg);
  }

  @Async
  @Override
  public void sendLateReturnNotification(User user, Book book, Long daysLate, double fine) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg =
        String.format(
            "Late Return: You returned '%s' %d day(s) late. Fine: $%.2f",
            book.getBookTitle(), daysLate, fine);
    sendEmail(email, "Library Late Return Notice", msg);
  }

  @Async
  @Override
  public void sendDueDateReminder(User user, Book book, Long daysLeft) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg;
    if (daysLeft == 0) {
      msg =
          String.format(
              "Reminder: Your borrowed book '%s' is due TODAY. Please return it to avoid fines.",
              book.getBookTitle());
    } else {
      msg =
          String.format(
              "Reminder: Your borrowed book '%s' is due in %d day(s). Please return it on time.",
              book.getBookTitle(), daysLeft);
    }
    sendEmail(email, "Library Due Date Reminder", msg);
  }
}
