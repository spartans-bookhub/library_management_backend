package com.spartans.controller;

import com.spartans.model.Notification;
import com.spartans.repository.NotificationRepository;
import com.spartans.util.UserContext;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

  private final NotificationRepository notificationRepository;

  public NotificationController(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @GetMapping()
  public List<Notification> getRecentNotifications() {
    Long userId = UserContext.getUserId();
    return notificationRepository.findTop10ByUserUserIdOrderByCreatedAtDesc(userId);
  }
}
