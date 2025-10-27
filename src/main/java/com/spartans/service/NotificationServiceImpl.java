package com.spartans.service;

import com.spartans.config.NotificationConfig;
import com.spartans.model.Book;
import com.spartans.model.Notification;
import com.spartans.model.User;
import com.spartans.repository.NotificationRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired NotificationRepository notificationRepository;

  @Autowired EmailService emailService;

  @Autowired NotificationConfig notificationConfig;

  // Generic method to save a notification
  private void saveNotification(User user, Book book, String type, String message, String status) {
    Notification notification = new Notification();
    notification.setUser(user);
    notification.setBook(book);
    notification.setType(type);
    notification.setMessage(message);
    notification.setStatus(status);

    if ("SENT".equals(status)) {
      notification.setSentAt(LocalDateTime.now());
    }

    notificationRepository.save(notification);
  }

  private void sendEmail(
      String to, String subject, String text, User user, Book book, String type) {
    if (to == null || to.isBlank()) {
      System.out.println("Cannot send email: recipient is null or empty.");
      saveNotification(user, book, type, text, "FAILED");
      return;
    }

    try {
      emailService.sendEmail(to, subject, text);
      System.out.println("Email sent to " + to + ": " + subject);
      saveNotification(user, book, type, text, "SENT");
    } catch (Exception e) {
      System.err.println("Failed to send email to " + to + ": " + e.getMessage());
      saveNotification(user, book, type, text, "FAILED");
    }
  }

  @Async
  @Override
  public void sendBookBorrowedNotification(User user, Book book) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg =
        String.format("Book Borrowed: You successfully borrowed '%s'.", book.getBookTitle());
    sendEmail(email, "Library Borrow Confirmation", msg, user, book, "BORROWED");
  }

  @Async
  @Override
  public void sendBookReturnedNotification(User user, Book book) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg = String.format("Book Returned: You returned '%s'. Thank you!", book.getBookTitle());
    sendEmail(email, "Library Return Confirmation", msg, user, book, "RETURNED");
  }

  @Async
  @Override
  public void sendLateReturnNotification(User user, Book book, Long daysLate, double fine) {
    String email = user.getUserAuth() != null ? user.getUserAuth().getEmail() : null;
    String msg =
        String.format(
            "Late Return: You returned '%s' %d day(s) late. Fine: Rs.%.2f",
            book.getBookTitle(), daysLate, fine);
    sendEmail(email, "Library Late Return Notice", msg, user, book, "LATE");
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
    sendEmail(email, "Library Due Date Reminder", msg, user, book, "REMINDER");
  }

  @Async
  @Override
  public void sendPasswordResetReminder(String email, String resetToken, String resetLink) {
    String subject = "Password Reset Link";
    String msg =
        String.format(
            "\n  To reset password, click the link below:  \n '%s' "
                + "  \nIf you did not request, please ignore this email.  \n",
            resetLink);
    sendEmail(email, subject, msg, "PASSWORD_RESET_LINK");
  }

  private void sendEmail(String to, String subject, String text, String type) {
    sendEmail(to, subject, text, null, null, type);
  }

  public void sendRegistrationEmail(String email, String name) {
    String subject = "Registration successful!";
    String msg =
        String.format(
            "\nWelcome to Book Nest, %s!  \n"
                + "\n Thank you for registering with Book Nest!  \n"
                + "\n Email: %s is successfully registered. You can now log in using your email and password.  \n",
            name, email);

    System.out.println(msg);
    System.out.println("email=" + email);
    sendEmail(email, subject, msg, "NEW_REGISTRATION");
  }

  public void sendResetSuccessEmail(String email) {
    String subject = "Password Reset successful!";
    String msg =
        String.format(
            "\n Email: %s   \n"
                + "\n Your password reset is successful. You can now log in using your email and password.  \n",
            email);

    System.out.println(msg);
    System.out.println("email=" + email);
    sendEmail(email, subject, msg, "PASSWORD_RESET_SUCCESS");
  }

  public void sendPasswordChangeEmail(String email) {
    String subject = "Password Change successful!";
    String msg =
        String.format(
            "\n Email: %s  \n"
                + "\n Your password change is successful. You can now log in using your email and password.  \n",
            email);

    System.out.println(msg);
    System.out.println("email=" + email);
    sendEmail(email, subject, msg, "PASSWORD_CHANGE_SUCCESS");
  }
}
