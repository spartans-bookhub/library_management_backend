// package com.spartans.service;
//
// import static org.assertj.core.api.Assertions.*;
//
// import com.spartans.dto.UserRequestDTO;
// import com.spartans.dto.UserResponseDTO;
// import com.spartans.exception.UserNotFoundException;
// import com.spartans.mapper.UserMapper;
// import com.spartans.model.User;
// import com.spartans.model.UserAuth;
// import com.spartans.repository.AuthRepository;
// import com.spartans.repository.UserRepository;
// import com.spartans.util.UserContext;
// import jakarta.persistence.EntityManager;
// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
//
// @SpringBootTest
// public class UserServiceTest {
//
//  @InjectMocks UserServiceImpl userService;
//
//  @Mock UserRepository userRepo;
//
//  @Mock AuthRepository authRepo;
//
//  @Mock UserMapper mapper;
//
//  @MockBean EntityManager entityManager;
//
//  AutoCloseable closable;
//  UserAuth userAuth = new UserAuth("john@test.com", "password", "STUDENT");
//  User user =
//      new User(
//          Long.valueOf(10),
//          "John",
//          LocalDateTime.now(),
//          "9099999990",
//          "456, main street, HR-6788",
//          userAuth);
//  User user2 =
//      new User(
//          Long.valueOf(10),
//          "Ben",
//          LocalDateTime.now(),
//          "909333990",
//          "45446, main street, HR-6788",
//          userAuth);
//
//  User user3 =
//      new User(
//          Long.valueOf(10),
//          "Paul",
//          LocalDateTime.now(),
//          "9095559990",
//          "555, main street, HR-67",
//          userAuth);
//  List<User> userList = List.of(user, user2, user3);
//
//  UserResponseDTO userResponseDTO =
//      new UserResponseDTO(
//          "John", "john@test.com", "9099999990", "456, main street, HR-6788", "STUDENT");
//  UserRequestDTO userRequestDTO =
//      new UserRequestDTO("John", "9099999990", "456, main street, HR-6788", "john@test.com");
//
//  @BeforeEach
//  void setUp() {
//    closable = MockitoAnnotations.openMocks(this);
//    Mockito.when(userRepo.findById(Long.valueOf(10))).thenReturn(Optional.of(user));
//    Mockito.when(mapper.toUserDto(user)).thenReturn(userResponseDTO);
//  }
//
//  @AfterEach
//  void tearDown() throws Exception {
//    closable.close();
//  }
//
//  // StudentResponseDTO getUser(Long id) - Success
//  @Test
//  void testGetUserSucccess() {
//    UserResponseDTO userDto = userService.getUser(Long.valueOf(10));
//
//    assertThat(userDto).isNotNull();
//    assertThat(userDto)
//        .extracting("userName", "email", "contactNumber", "address", "role")
//        .containsExactly(
//            "John", "john@test.com", "9099999990", "456, main street, HR-6788", "STUDENT");
//  }
//
//  // StudentResponseDTO getUser(Long id) - UserNotFoundException
//  @Test
//  void testGetUserNotFoundById() {
//    Mockito.when(userRepo.findById(Long.valueOf(10))).thenReturn(Optional.empty());
//    assertThatThrownBy(() -> userService.getUser(Long.valueOf(10)))
//        .isInstanceOf(UserNotFoundException.class)
//        .hasMessageContaining("User not found with this email");
//  }
//
//  // UserResponseDTO editUser(UserRequestDTO request, Long id) - success
//  @Test
//  void testEditUserSuccess() {
//    UserContext.setUser(Map.of("email", "john@test.com"));
//    Mockito.when(userRepo.findById(Long.valueOf(10))).thenReturn(Optional.of(user));
//    Mockito.when(authRepo.findById("john@test.com")).thenReturn(Optional.of(userAuth));
//    Mockito.when(userRepo.save(user)).thenReturn(user);
//    Mockito.when(mapper.toUserDto(user)).thenReturn(userResponseDTO);
//    UserResponseDTO userResponse = userService.editUser(userRequestDTO, Long.valueOf(10));
//    assertThat(userResponse).isNotNull();
//    assertThat(userResponse)
//        .extracting("userName", "email", "contactNumber", "address", "role")
//        .containsExactly(
//            "John", "john@test.com", "9099999990", "456, main street, HR-6788", "STUDENT");
//  }
//
//  // UserResponseDTO editUser(UserRequestDTO request, Long id) - UserNotFoundException
//  @Test
//  void testEditUserUserNotFound() {
//    assertThatThrownBy(() -> userService.editUser(userRequestDTO, Long.valueOf(10)))
//        .isInstanceOf(UserNotFoundException.class)
//        .hasMessageContaining("Incorrect user. This user doesn't exist");
//    assertThatThrownBy(() -> userService.editUser(userRequestDTO, Long.valueOf(10)))
//        .isInstanceOf(UserNotFoundException.class)
//        .hasMessageContaining("Incorrect user. This user doesn't exist");
//  }
//
//  // public List<UserResponseDTO> getAllUsers()
//  @Test
//  void testGetAllUser() {
//    UserResponseDTO responseDto1 =
//        new UserResponseDTO(
//            "John", "john@test.com", "9099999990", "456, main street, HR-6788", "STUDENT");
//    UserResponseDTO responseDto2 =
//        new UserResponseDTO(
//            "Ben", "john@test.com", "909333990", "45446, main street, HR-6788", "STUDENT");
//    UserResponseDTO responseDto3 =
//        new UserResponseDTO(
//            "Paul", "john@test.com", "9095559990", "555, main street, HR-67", "STUDENT");
//    Mockito.when(userRepo.findAll()).thenReturn(userList);
//    Mockito.when(mapper.toUserDto(user)).thenReturn(responseDto1);
//    Mockito.when(mapper.toUserDto(user2)).thenReturn(responseDto2);
//    Mockito.when(mapper.toUserDto(user3)).thenReturn(responseDto3);
//    List<UserResponseDTO> usersDto = userService.getAllUsers();
//
//    assertThat(usersDto).isNotNull();
//    assertThat(usersDto)
//        .extracting("userName", "email", "contactNumber", "address", "role")
//        .containsExactly(
//            tuple("John", "john@test.com", "9099999990", "456, main street, HR-6788", "STUDENT"),
//            tuple("Ben", "john@test.com", "909333990", "45446, main street, HR-6788", "STUDENT"),
//            tuple("Paul", "john@test.com", "9095559990", "555, main street, HR-67", "STUDENT"));
//  }
//
//  // public List<UserResponseDTO> getAllUsers() - No User exists
//  @Test
//  void testGetAllUsersWhenNoUserExists() {
//    Mockito.when(userRepo.findAll()).thenReturn(Collections.emptyList());
//    List<UserResponseDTO> usersDto = userService.getAllUsers();
//    assertThat(usersDto).isEmpty();
//  }
//
//  // public boolean deleteUser(Long id)
//
//  @Test
//  void testDeleteUserSuccess() {
//    Mockito.when(userRepo.existsById(Long.valueOf(10))).thenReturn(true);
//    Mockito.doNothing().when(userRepo).deleteById(Long.valueOf(10));
//    boolean deleted = userService.deleteUser(Long.valueOf(10));
//    assertThat(deleted).isTrue();
//  }
//
//  // public boolean deleteUser(Long id) - user id doesn nto exists
//  @Test
//  void testDeleteUserFail() {
//    Mockito.when(userRepo.existsById(Long.valueOf(10))).thenReturn(false);
//    Mockito.doNothing().when(userRepo).deleteById(Long.valueOf(10));
//
//    assertThatThrownBy(() -> userService.deleteUser(Long.valueOf(10)))
//        .isInstanceOf(UserNotFoundException.class)
//        .hasMessageContaining("This user is not found");
//  }
// }
