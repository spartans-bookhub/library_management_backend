package com.spartans.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spartans.dto.*;
import com.spartans.exception.DBException;
import com.spartans.exception.InvalidLoginException;
import com.spartans.exception.UserAlreadyExistException;
import com.spartans.exception.UserNotFoundException;
import com.spartans.mapper.UserMapper;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import com.spartans.util.JWTUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthServiceTest {

  @InjectMocks private AuthServiceImpl authService;

  @Mock private AuthRepository authRepo;

  @Mock private UserMapper mapper;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JWTUtils jwtUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // --- register() ---

  @Test
  void register_ShouldThrowUserAlreadyExistException_WhenEmailExists() {
    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
    when(request.email()).thenReturn("test@example.com");
    when(authRepo.existsById("test@example.com")).thenReturn(true);

    assertThrows(UserAlreadyExistException.class, () -> authService.register(request));
  }

  @Test
  void register_ShouldReturnTrue_WhenUserRegisteredSuccessfully() {
    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
    when(request.email()).thenReturn("test@example.com");
    when(request.password()).thenReturn("password");

    when(authRepo.existsById("test@example.com")).thenReturn(false);

    UserAuth userAuth = new UserAuth();
    User student = new User();

    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
    when(mapper.toUserEntity(request)).thenReturn(student);

    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

    when(authRepo.save(userAuth)).thenReturn(userAuth);

    boolean result = authService.register(request);

    assertTrue(result);
    assertEquals("encodedPassword", userAuth.getPassword());
    assertNotNull(student.getCreatedAt());
    assertEquals(student, userAuth.getStudent());
    assertEquals(userAuth, student.getUserAuth());
  }

  @Test
  void register_ShouldThrowUserAlreadyExistException_WhenDataIntegrityViolationException() {
    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
    when(request.email()).thenReturn("test@example.com");
    when(authRepo.existsById("test@example.com")).thenReturn(false);
    UserAuth userAuth = new UserAuth();
    User student = new User();
    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
    when(mapper.toUserEntity(request)).thenReturn(student);
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");

    doThrow(DataIntegrityViolationException.class).when(authRepo).save(userAuth);

    assertThrows(UserAlreadyExistException.class, () -> authService.register(request));
  }

  @Test
  void register_ShouldThrowDBException_OnGenericException() {
    RegisterRequestDTO request = mock(RegisterRequestDTO.class);
    when(request.email()).thenReturn("test@example.com");
    when(authRepo.existsById("test@example.com")).thenReturn(false);
    UserAuth userAuth = new UserAuth();
    User student = new User();
    when(mapper.toUserAuthEntity(request)).thenReturn(userAuth);
    when(mapper.toUserEntity(request)).thenReturn(student);
    when(passwordEncoder.encode(anyString())).thenReturn("encoded");

    doThrow(new RuntimeException()).when(authRepo).save(userAuth);

    assertThrows(DBException.class, () -> authService.register(request));
  }

  // --- login() ---

  @Test
  void login_ShouldThrowUserNotFoundException_WhenEmailNotFound() {
    LoginRequestDTO request = mock(LoginRequestDTO.class);
    when(request.email()).thenReturn("notfound@example.com");
    when(authRepo.findById("notfound@example.com")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> authService.login(request));
  }

  @Test
  void login_ShouldThrowUserNotFoundException_WhenStudentNotFoundForStudentRole() {
    LoginRequestDTO request = mock(LoginRequestDTO.class);
    when(request.email()).thenReturn("student@example.com");

    UserAuth userAuth = new UserAuth();
    userAuth.setRole("STUDENT");
    userAuth.setStudent(null); // student missing

    when(authRepo.findById("student@example.com")).thenReturn(Optional.of(userAuth));

    assertThrows(UserNotFoundException.class, () -> authService.login(request));
  }

  @Test
  void login_ShouldThrowInvalidLoginException_WhenPasswordDoesNotMatch() {
    LoginRequestDTO request = mock(LoginRequestDTO.class);
    when(request.email()).thenReturn("user@example.com");
    when(request.password()).thenReturn("wrongPassword");

    UserAuth userAuth = new UserAuth();
    userAuth.setRole("STUDENT");
    userAuth.setStudent(new User());
    userAuth.setPassword("encodedPassword");

    when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
    when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

    assertThrows(InvalidLoginException.class, () -> authService.login(request));
  }

  @Test
  void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
    LoginRequestDTO request = mock(LoginRequestDTO.class);
    when(request.email()).thenReturn("user@example.com");
    when(request.password()).thenReturn("correctPassword");

    UserAuth userAuth = new UserAuth();
    userAuth.setRole("STUDENT");
    User student = new User();
    userAuth.setStudent(student);
    userAuth.setEmail("user@example.com");
    userAuth.setPassword("encodedPassword");

    when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
    when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
    when(jwtUtil.generateToken(userAuth)).thenReturn("token123");

    LoginResponseDTO responseDTO = mock(LoginResponseDTO.class);
    when(mapper.toLoginDto(student, "user@example.com", "STUDENT", "token123"))
        .thenReturn(responseDTO);

    LoginResponseDTO result = authService.login(request);

    assertEquals(responseDTO, result);
  }

  // --- changePassword() ---

  //    @Test
  //    void changePassword_ShouldThrowUserNotFoundException_WhenUserNotFound() {
  //        PasswordRequestDto request = mock(PasswordRequestDto.class);
  //        when(UserContext.getEmail()).thenReturn("notfound@example.com");
  //        when(authRepo.findById("notfound@example.com")).thenReturn(Optional.empty());
  //
  //        assertThrows(UserNotFoundException.class, () -> authService.changePassword(request));
  //    }

  //    @Test
  //    void changePassword_ShouldReturnFalse_WhenOldPasswordIsInvalid() {
  //        PasswordRequestDto request = mock(PasswordRequestDto.class);
  //        when(request.oldPassword()).thenReturn("wrongOldPass");
  //        when(UserContext.getEmail()).thenReturn("user@example.com");
  //
  //        UserAuth userAuth = new UserAuth();
  //        userAuth.setPassword("encodedPassword");
  //
  //        when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
  //        when(passwordEncoder.matches("wrongOldPass", "encodedPassword")).thenReturn(false);
  //
  //        boolean result = authService.changePassword(request);
  //
  //        assertFalse(result);
  //    }

  //    @Test
  //    void changePassword_ShouldReturnTrue_WhenPasswordChangedSuccessfully() {
  //        PasswordRequestDto request = mock(PasswordRequestDto.class);
  //        when(request.oldPassword()).thenReturn("oldPass");
  //        when(request.newPassword()).thenReturn("newPass");
  //        when(UserContext.getEmail()).thenReturn("user@example.com");
  //
  //        UserAuth userAuth = new UserAuth();
  //        userAuth.setPassword("encodedOldPass");
  //
  //        when(authRepo.findById("user@example.com")).thenReturn(Optional.of(userAuth));
  //        when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
  //        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
  //        when(authRepo.save(userAuth)).thenReturn(userAuth);
  //
  //        boolean result = authService.changePassword(request);
  //
  //        assertTrue(result);
  //        assertEquals("encodedNewPass", userAuth.getPassword());
  //    }
}
