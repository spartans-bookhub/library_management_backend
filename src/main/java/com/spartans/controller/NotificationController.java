package com.spartans.controller;

import com.spartans.dto.NotificationDTO;
import com.spartans.model.Notification;
import com.spartans.repository.NotificationRepository;
import com.spartans.util.UserContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping()
    public List<NotificationDTO> getRecentNotifications() {
        Long userId = UserContext.getUserId();
        if (userId == null) return Collections.emptyList(); // null-safe

        List<Notification> notifications =
                notificationRepository.findTop10ByUserUserIdOrderByCreatedAtDesc(userId);

        if (notifications == null || notifications.isEmpty()) {
            return Collections.emptyList(); // null-safe
        }

        return notifications.stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getMessage(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}