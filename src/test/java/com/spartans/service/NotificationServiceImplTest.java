package com.spartans.service;

import com.spartans.model.Book;
import com.spartans.model.Notification;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import com.spartans.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    private JavaMailSender mailSender;
    private NotificationRepository notificationRepository;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        notificationRepository = mock(NotificationRepository.class);

        notificationService = new NotificationServiceImpl();
        notificationService.mailSender = mailSender; // inject mock
        notificationService.notificationRepository = notificationRepository; // inject mock
    }

    @Test
    void testSendBookBorrowedNotification() {
        UserAuth auth = new UserAuth();
        auth.setEmail("user@example.com");

        User user = new User();
        user.setUserAuth(auth);

        Book book = new Book();
        book.setBookTitle("Mockito for Beginners");

        notificationService.sendBookBorrowedNotification(user, book);

        // Verify email was sent
        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(emailCaptor.capture());

        SimpleMailMessage sentMessage = emailCaptor.getValue();
        assertEquals("user@example.com", sentMessage.getTo()[0]);
        assertEquals("Library Borrow Confirmation", sentMessage.getSubject());
        assertEquals("Book Borrowed: You successfully borrowed 'Mockito for Beginners'.", sentMessage.getText());

        // Verify notification was saved
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertEquals(user, savedNotification.getUser());
        assertEquals(book, savedNotification.getBook());
        assertEquals("BORROWED", savedNotification.getType());
        assertEquals("Book Borrowed: You successfully borrowed 'Mockito for Beginners'.", savedNotification.getMessage());
        assertEquals("SENT", savedNotification.getStatus());
    }

    @Test
    void testSendEmailWithNullRecipient() {
        // User without email
        User user = new User();
        user.setUserAuth(null);

        Book book = new Book();
        book.setBookTitle("Mockito for Beginners");

        notificationService.sendBookBorrowedNotification(user, book);

        // Email should never be sent
        verify(mailSender, never()).send(any(SimpleMailMessage.class));

        // Notification should still be saved with FAILED status
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());

        Notification savedNotification = notificationCaptor.getValue();
        assertEquals(user, savedNotification.getUser());
        assertEquals(book, savedNotification.getBook());
        assertEquals("BORROWED", savedNotification.getType());
        assertEquals("Book Borrowed: You successfully borrowed 'Mockito for Beginners'.", savedNotification.getMessage());
        assertEquals("FAILED", savedNotification.getStatus());
    }
}