// package com.spartans.service;
//
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import com.spartans.dto.LoginRequestDTO;
// import com.spartans.dto.LoginResponseDTO;
// import com.spartans.dto.PasswordRequestDTO;
// import com.spartans.dto.RegisterRequestDTO;
// import com.spartans.exception.DBException;
// import com.spartans.exception.InvalidLoginException;
// import com.spartans.exception.UserAlreadyExistException;
// import com.spartans.exception.UserNotFoundException;
// import com.spartans.mapper.UserMapper;
// import com.spartans.model.User;
// import com.spartans.model.UserAuth;
// import com.spartans.repository.AuthRepository;
// import com.spartans.util.JWTUtils;
// import com.spartans.util.UserContext;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @ExtendWith(MockitoExtension.class)
// class AuthServiceTest {
//
//  @InjectMocks private AuthServiceImpl authService;
//
//  @Mock private AuthRepository authRepo;
//
//  @Mock private UserMapper mapper;
//
//  @Mock private PasswordEncoder passwordEncoder;
//
//  @Mock private JWTUtils jwtUtil;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  @Test
//  void testRegisterUserAlreadyExistException() {
//    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
//    when(request.email()).thenReturn("test@example.com");
//    when(authRepo.existsById("test@example.com")).thenReturn(true);
//
//    assertThrows(UserAlreadyExistException.class, () -> authService.register(request));
//  }
//
//  @Test
//  void testRegisterSuccess() {
//    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
//    when(request.email()).thenReturn("test@example.com");
//    when(request.password()).thenReturn("password");
//
//    when(authRepo.existsById("test@example.com")).thenReturn(false);
//
//    UserAuth userAuth = new UserAuth();
//    User student = new User();
//
//    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
//    when(mapper.toUserEntity(request)).thenReturn(student);
//
//    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
//
//    when(authRepo.save(userAuth)).thenReturn(userAuth);
//
//    boolean result = authService.register(request);
//
//    assertTrue(result);
//    assertEquals("encodedPassword", userAuth.getPassword());
//    assertNotNull(student.getCreatedAt());
//    assertEquals(student, userAuth.getStudent());
//    assertEquals(userAuth, student.getUserAuth());
//  }
//
//  @Test
//  void testRegisterUserAlreadyExist() {
//    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
//    when(request.email()).thenReturn("test@example.com");
//    when(request.password()).thenReturn("password");
//    when(authRepo.existsById("test@example.com")).thenReturn(false);
//    UserAuth userAuth = new UserAuth();
//    User student = new User();
//    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
//    when(mapper.toUserEntity(request)).thenReturn(student);
//    when(passwordEncoder.encode(anyString())).thenReturn("encoded");
//
//    doThrow(DataIntegrityViolationException.class).when(authRepo).save(userAuth);
//
//    assertThrows(UserAlreadyExistException.class, () -> authService.register(request));
//  }
//
//  @Test
//  void testRegisterGenericException() {
//    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
//    when(request.email()).thenReturn("test@example.com");
//    when(authRepo.existsById("test@example.com")).thenReturn(false);
//    UserAuth userAuth = new UserAuth();
//    User user = new User();
//    when(request.password()).thenReturn("password");
//    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
//    when(mapper.toUserEntity(request)).thenReturn(user);
//    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//    when(authRepo.save(any())).thenThrow(new DBException("Failed to save user"));
//
//    assertThatThrownBy(() -> authService.register(request))
//        .isInstanceOf(DBException.class)
//        .hasMessageContaining("Failed to save user");
//  }
//
//  @Test
//  void testLoginUserNotFound() {
//    LoginRequestDTO request = mock(LoginRequestDTO.class);
//    when(request.email()).thenReturn("notfound@example.com");
//    when(authRepo.findById("notfound@example.com")).thenReturn(Optional.empty());
//
//    assertThrows(UserNotFoundException.class, () -> authService.login(request));
//  }
//
//  @Test
//  void testLoginUserNotFoundStudentNotFoundForStudentRole() {
//    LoginRequestDTO request = mock(LoginRequestDTO.class);
//    when(request.email()).thenReturn("student@example.com");
//
//    UserAuth userAuth = new UserAuth();
//    userAuth.setRole("STUDENT");
//    userAuth.setStudent(null); // student missing
//
//    when(authRepo.findById("student@example.com")).thenReturn(Optional.of(userAuth));
//
//    assertThrows(UserNotFoundException.class, () -> authService.login(request));
//  }
//
//  @Test
//  void testLoginInvalidLogin() {
//    LoginRequestDTO request = mock(LoginRequestDTO.class);
//    when(request.email()).thenReturn("user@example.com");
//    when(request.password()).thenReturn("wrongPassword");
//
//    UserAuth userAuth = new UserAuth();
//    userAuth.setRole("STUDENT");
//    userAuth.setStudent(new User());
//    userAuth.setPassword("encodedPassword");
//
//    when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
//    when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
//
//    assertThrows(InvalidLoginException.class, () -> authService.login(request));
//  }
//
//  @Test
//  void testLoginSuccess() {
//    LoginRequestDTO request = mock(LoginRequestDTO.class);
//    when(request.email()).thenReturn("user@example.com");
//    when(request.password()).thenReturn("correctPassword");
//
//    UserAuth userAuth = new UserAuth();
//    userAuth.setRole("STUDENT");
//    User student = new User();
//    userAuth.setStudent(student);
//    userAuth.setEmail("user@example.com");
//    userAuth.setPassword("encodedPassword");
//
//    when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
//    when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
//    when(jwtUtil.generateToken(userAuth)).thenReturn("token123");
//
//    LoginResponseDTO responseDTO = mock(LoginResponseDTO.class);
//    when(mapper.toLoginDto(student, "user@example.com", "STUDENT", "token123"))
//        .thenReturn(responseDTO);
//
//    LoginResponseDTO result = authService.login(request);
//
//    assertEquals(responseDTO, result);
//  }
//
//  @Test
//  void changePasswordSuccess() {
//    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
//      mockedUserContext.when(UserContext::getEmail).thenReturn("user@example.com");
//
//      UserAuth userAuth = new UserAuth();
//      userAuth.setPassword("encodedOldPassword");
//
//      PasswordRequestDTO request = new PasswordRequestDTO("OldPass1!", "NewPass2@", "NewPass2@");
//
//      when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
//      when(passwordEncoder.matches("OldPass1!", "encodedOldPassword")).thenReturn(true);
//      when(passwordEncoder.encode("NewPass2@")).thenReturn("encodedNewPassword");
//      when(authRepo.save(any(UserAuth.class))).thenAnswer(invocation ->
// invocation.getArgument(0));
//
//      boolean result = authService.changePassword(request);
//
//      assertTrue(result);
//      assertEquals("encodedNewPassword", userAuth.getPassword());
//
//      verify(authRepo).findById("user@example.com");
//      verify(passwordEncoder).matches("OldPass1!", "encodedOldPassword");
//      verify(passwordEncoder).encode("NewPass2@");
//      verify(authRepo).save(userAuth);
//    }
//  }
//
//  @Test
//  void changePasswordUserNotFound() {
//    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
//      mockedUserContext.when(UserContext::getEmail).thenReturn("user@example.com");
//      String email = "user@example.com";
//
//      PasswordRequestDTO request = new PasswordRequestDTO("OldPass1!", "NewPass2@", "NewPass2@");
//
//      when(authRepo.findById(email)).thenReturn(Optional.empty());
//
//      UserNotFoundException ex =
//          assertThrows(
//              UserNotFoundException.class,
//              () -> {
//                authService.changePassword(request);
//              });
//      assertEquals("This user is not registered", ex.getMessage());
//
//      verify(authRepo).findById(email);
//      verifyNoMoreInteractions(passwordEncoder, authRepo);
//    }
//  }
//
//  @Test
//  void changePassword_newPasswordsMismatch_throwsIllegalArgumentException() {
//    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
//      mockedUserContext.when(UserContext::getEmail).thenReturn("user@example.com");
//      String email = "user@example.com";
//
//      UserAuth userAuth = new UserAuth();
//      userAuth.setPassword("encodedOldPassword");
//
//      PasswordRequestDTO request =
//          new PasswordRequestDTO("OldPass1!", "NewPass2@", "MismatchNewPass!");
//
//      when(authRepo.findById(email)).thenReturn(Optional.of(userAuth));
//
//      IllegalArgumentException ex =
//          assertThrows(
//              IllegalArgumentException.class,
//              () -> {
//                authService.changePassword(request);
//              });
//      assertEquals("New passwords do not match", ex.getMessage());
//
//      verify(authRepo).findById(email);
//      verifyNoMoreInteractions(passwordEncoder, authRepo);
//    }
//  }
//
//  @Test
//  void changePassword_oldPasswordIncorrect_throwsInvalidLoginException() {
//    try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
//      mockedUserContext.when(UserContext::getEmail).thenReturn("user@example.com");
//      String email = "user@example.com";
//
//      UserAuth userAuth = new UserAuth();
//      userAuth.setPassword("encodedOldPassword");
//
//      PasswordRequestDTO request =
//          new PasswordRequestDTO("WrongOldPass!", "NewPass2@", "NewPass2@");
//
//      when(authRepo.findById(email)).thenReturn(Optional.of(userAuth));
//      when(passwordEncoder.matches("WrongOldPass!", "encodedOldPassword")).thenReturn(false);
//
//      InvalidLoginException ex =
//          assertThrows(
//              InvalidLoginException.class,
//              () -> {
//                authService.changePassword(request);
//              });
//      assertEquals("Login email or password is wrong", ex.getMessage());
//
//      verify(authRepo).findById(email);
//      verify(passwordEncoder).matches("WrongOldPass!", "encodedOldPassword");
//      verifyNoMoreInteractions(authRepo);
//    }
//  }
// }
