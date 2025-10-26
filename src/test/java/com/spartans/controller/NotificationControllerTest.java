package com.spartans.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.NotificationDTO;
import com.spartans.model.Notification;
import com.spartans.model.User;
import com.spartans.repository.NotificationRepository;
import com.spartans.util.UserContext;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class NotificationControllerTest {

  private NotificationRepository notificationRepository;
  private NotificationController controller;

  @BeforeEach
  void setup() {
    notificationRepository = mock(NotificationRepository.class);
    controller = new NotificationController(notificationRepository);
  }

  @Test
  void testGetRecentNotifications() {
    Long userId = 1L;

    Notification n1 = new Notification();
    n1.setId(101L);
    n1.setMessage("Notification 1");
    n1.setCreatedAt(LocalDateTime.now().minusDays(1));
    n1.setUser(new User());

    Notification n2 = new Notification();
    n2.setId(102L);
    n2.setMessage("Notification 2");
    n2.setCreatedAt(LocalDateTime.now());

    List<Notification> mockList = List.of(n1, n2);

    when(notificationRepository.findTop10ByUserUserIdOrderByCreatedAtDesc(userId))
        .thenReturn(mockList);

    try (MockedStatic<UserContext> mockedUserContext = Mockito.mockStatic(UserContext.class)) {
      mockedUserContext.when(UserContext::getUserId).thenReturn(userId);

      List<NotificationDTO> response = controller.getRecentNotifications();

      assertNotNull(response);
      assertEquals(2, response.size());

      assertEquals(101L, response.get(0).getId());
      assertEquals("Notification 1", response.get(0).getMessage());

      assertEquals(102L, response.get(1).getId());
      assertEquals("Notification 2", response.get(1).getMessage());
    }

    verify(notificationRepository).findTop10ByUserUserIdOrderByCreatedAtDesc(userId);
  }
}
