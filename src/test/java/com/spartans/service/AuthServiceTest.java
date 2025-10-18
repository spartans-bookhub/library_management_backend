package com.spartans.service;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.InvalidLoginException;
import com.spartans.exception.UserAlreadyExistException;
import com.spartans.exception.UserNotFoundException;
import com.spartans.mapper.DTOMapper;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import com.spartans.util.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceTest {

    @Mock
    private AuthRepository authRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtils jwtUtil;

    @Mock
    private DTOMapper mapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "pass");
        UserAuth userAuth = new UserAuth();
        userAuth.setLoginId("test@test.com");
        userAuth.setPassword("encodedPass");
        userAuth.setRole("STUDENT");
        userAuth.setStudent(new User());

        when(authRepo.findById("test@test.com")).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches("pass", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken("test@test.com", "STUDENT", userAuth.getStudent())).thenReturn("token123");
        LoginResponseDTO responseDTO = new LoginResponseDTO("test@test.com", "STUDENT", "token123");
        when(mapper.toLoginResponseDto(userAuth, "token123")).thenReturn(responseDTO);

        LoginResponseDTO result = authService.login(request);
        assertNotNull(result);
        verify(authRepo).findById("test@test.com");
        verify(passwordEncoder).matches("pass", "encodedPass");
        verify(jwtUtil).generateToken("test@test.com", "STUDENT", userAuth.getStudent());
        verify(mapper).toLoginResponseDto(userAuth, "token123");
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "pass");
        when(authRepo.findById("test@test.com")).thenReturn(Optional.empty());

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid login", thrown.getMessage());
    }

    @Test
    void testLoginStudentNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "pass");
        UserAuth userAuth = new UserAuth();
        userAuth.setLoginId("test@test.com");
        userAuth.setRole("STUDENT");
        userAuth.setStudent(null);  // Student missing

        when(authRepo.findById("test@test.com")).thenReturn(Optional.of(userAuth));

        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> {
            authService.login(request);
        });

        assertEquals("Student is not found", thrown.getMessage());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "wrongPass");
        UserAuth userAuth = new UserAuth();
        userAuth.setLoginId("test@test.com");
        userAuth.setPassword("encodedPass");
        userAuth.setRole("STUDENT");
        userAuth.setStudent(new User());

        when(authRepo.findById("test@test.com")).thenReturn(Optional.of(userAuth));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        InvalidLoginException thrown = assertThrows(InvalidLoginException.class, () -> {
            authService.login(request);
        });

        assertEquals("Login Id or password is wrong", thrown.getMessage());
    }

    @Test
    void testRegisterSuccess() {

        RegisterRequestDTO request = new RegisterRequestDTO("test@test.com", "password", "test");
        UserAuth userAuth = new UserAuth();
        User student = new User();
        userAuth.setStudent(student);
        StudentResponseDTO expectedResponse = new StudentResponseDTO(Long.valueOf(1000), "test", "test@test.com", "12345", "34street 3445");
        when(authRepo.existsById("test@test.com")).thenReturn(false);
        when(mapper.toAuth(request)).thenReturn(userAuth);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(mapper.toStudent(request)).thenReturn(student);
        when(authRepo.save(userAuth)).thenReturn(userAuth);
        when(mapper.toStudentDto(student)).thenReturn(expectedResponse);

        StudentResponseDTO actualResponse = authService.register(request);
        assertNotNull(actualResponse);
        assertEquals("test", actualResponse.name());
        assertEquals("test@test.com", actualResponse.email());
        assertEquals(1000, actualResponse.studentId());

        verify(authRepo).existsById("test@test.com");
        verify(passwordEncoder).encode("password");
        verify(authRepo).save(userAuth);
        verify(mapper).toStudentDto(student);
    }

    @Test
    void testRegisterUserAlreadyExistsThrowsException() {

        RegisterRequestDTO request = new RegisterRequestDTO("test@test.com", "password", "test");
        when(authRepo.existsById("test@test.com")).thenReturn(true);
        assertThrows(UserAlreadyExistException.class, () -> {
            authService.register(request);
        });
        verify(authRepo).existsById("test@test.com");
        verifyNoMoreInteractions(authRepo, mapper, passwordEncoder);
    }

}
