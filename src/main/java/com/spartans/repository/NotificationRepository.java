package com.spartans.repository;

import com.spartans.model.Book;
import com.spartans.model.Notification;
import com.spartans.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  // Find all notifications for a user
  List<Notification> findByUser(User user);

  // Find all notifications for a user and book
  List<Notification> findByUserAndBook(User user, Book book);

  // Find all notifications by status
  List<Notification> findByStatus(String status);

    List<Notification> findTop10ByUserUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findTop10ByUser_UserIdOrderByCreatedAtDesc(Long userId);
    //  List<Notification> findTop10ByUserOrderByCreatedAtDesc(User user);
}
