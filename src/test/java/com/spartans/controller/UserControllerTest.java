package com.spartans.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartans.dto.*;
import com.spartans.exception.*;
import com.spartans.service.UserService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

public class UserControllerTest {

  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void testViewProfileSuccess() throws Exception {
    Long userId = 1L;
    UserResponseDTO userDto =
        new UserResponseDTO(
            "John Doe", "john@example.com", "9876543210", "Some Address", "STUDENT");

    when(userService.getUser(userId)).thenReturn(userDto);

    mockMvc
        .perform(get("/api/user/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void testViewProfileUserNotFound() throws Exception {
    Long userId = 1L;

    when(userService.getUser(userId))
        .thenThrow(new UserNotFoundException("User not found with this email"));

    mockMvc
        .perform(get("/api/user/{id}", userId))
        .andExpect(status().isNotFound())
        .andExpect(content().string("User not found with this email"));
  }

  @Test
  void testEditProfileSuccess() throws Exception {
    Long userId = 1L;
    UserRequestDTO request =
        new UserRequestDTO(10, "John Doe", "9876543210", "Some Address", "john@example.com");
    UserResponseDTO updatedDto =
        new UserResponseDTO(
            "John Doe", "john@example.com", "9876543210", "Some Address", "STUDENT");

    when(userService.editUser(any(UserRequestDTO.class))).thenReturn(updatedDto);

    mockMvc
        .perform(
            put("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void testEditProfileUserNotFound() throws Exception {
    Long userId = 1L;
    UserRequestDTO request =
        new UserRequestDTO(10, "John Doe", "9876543210", "Some Address", "john@example.com");

    when(userService.editUser(any(UserRequestDTO.class)))
        .thenThrow(new UserNotFoundException("Incorrect user. This user doesn't exist"));

    mockMvc
        .perform(
            put("/api/user", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().string("Incorrect user. This user doesn't exist"));
  }

  @Test
  void testGetAllUsersSuccess() throws Exception {
    List<UserResponseDTO> users =
        List.of(
            new UserResponseDTO("John Doe", "john@example.com", "9876543210", "Addr1", "STUDENT"),
            new UserResponseDTO("Jane Smith", "jane@example.com", "9876543211", "Addr2", "ADMIN"));

    when(userService.getAllUsers()).thenReturn(users);

    mockMvc
        .perform(get("/api/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].userName").value("John Doe"))
        .andExpect(jsonPath("$[1].userName").value("Jane Smith"));
  }

  @Test
  void testDeleteUserSuccess() throws Exception {
    Long userId = 1L;

    when(userService.deleteUser(userId)).thenReturn(true);

    mockMvc
        .perform(delete("/api/user/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  void testDeleteUserUserNotFound() throws Exception {
    Long userId = 1L;

    when(userService.deleteUser(userId))
        .thenThrow(new UserNotFoundException("This user is not found"));

    mockMvc
        .perform(delete("/api/user/{id}", userId))
        .andExpect(status().isNotFound())
        .andExpect(content().string("This user is not found"));
  }
}
