// package com.spartans.controller;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.spartans.dto.LoginRequestDTO;
// import com.spartans.dto.LoginResponseDTO;
// import com.spartans.dto.PasswordRequestDTO;
// import com.spartans.dto.RegisterRequestDTO;
// import com.spartans.exception.*;
// import com.spartans.service.AuthServiceImpl;
// import com.spartans.util.UserContext;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
//
// public class AuthControllerTest {
//
//    @InjectMocks private AuthController authController;
//
//    @Mock private AuthServiceImpl authService;
//
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @Mock UserContext userContext;
//
//    private RegisterRequestDTO validReq;
//    private RegisterRequestDTO invalidReq;
//    private LoginResponseDTO validResponseDto;
//    private LoginRequestDTO validLoginRequest;
//    private LoginRequestDTO invalidLoginRequest;
//    private PasswordRequestDTO validPassRequest;
//    private PasswordRequestDTO invalidPassRequest;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc =
//                MockMvcBuilders.standaloneSetup(authController)
//                        .setControllerAdvice(new GlobalExceptionHandler()) // if you have
// exception handlers
//                        .build();
//        validReq =
//                new RegisterRequestDTO(
//                        "test@example.com", "Password@123", "John", "919876543210", "address1
// 2345");
//        invalidReq =
//                new RegisterRequestDTO(
//                        "invalid-email", // invalid email format
//                        "pass", // invalid password (too short, no special char etc)
//                        "", // blank name
//                        "123456", // invalid phone number (doesn't match pattern)
//                        "some address");
//
//        validLoginRequest = new LoginRequestDTO("test@test.com", "Password@123");
//        invalidLoginRequest =
//                new LoginRequestDTO(
//                        "invalid-email", // invalid email format
//                        "Passsword@123" // invalid password (too short, no special char etc)
//                );
//        validResponseDto =
//                new LoginResponseDTO(
//                        Long.valueOf(123),
//                        "john@test.com",
//                        "STUDENT",
//                        "jwttoken",
//                        "John",
//                        "9876543210",
//                        "123 adress street 4576");
//        validPassRequest = new PasswordRequestDTO("Password@123", "Password@456", "Password@456");
//    }
//
//    @Test
//    void testRegisterSuccess() throws Exception {
//        when(authService.register(validReq)).thenReturn(true);
//
//        mockMvc
//                .perform(
//                        post("/register")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(validReq)))
//                .andExpect(status().isCreated())
//                .andExpect(content().string("User registered successfully"));
//        ;
//    }
//
//    @Test
//    void testRegisterValidationFail() throws Exception {
//        mockMvc
//                .perform(
//                        post("/register")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(invalidReq)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testLoginInvalidPassword_() throws Exception {
//        LoginRequestDTO validLoginRequest =
//                new LoginRequestDTO("user@example.com", "WrongPassword@123");
//
//        when(authService.login(any(LoginRequestDTO.class)))
//                .thenThrow(new IllegalArgumentException("Invalid credentials"));
//
//        mockMvc
//                .perform(
//                        post("/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(validLoginRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("Invalid credentials"));
//    }
//
//    @Test
//    void testLoginInvalidInput() throws Exception {
//        // blank email and password
//        LoginRequestDTO invalidLoginRequest = new LoginRequestDTO("", "");
//
//        mockMvc
//                .perform(
//                        post("/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(invalidLoginRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.email").value("Login email cannot be blank"))
//                .andExpect(jsonPath("$.password").value("Password cannot be blank"));
//    }
//
//    @Test
//    void testRegisterUserAlreadyExists() throws Exception {
//        RegisterRequestDTO validRequest =
//                new RegisterRequestDTO(
//                        "existing@example.com", "Password@123", "John Doe", "+919876543210", "123
// Street");
//
//        when(authService.register(validRequest))
//                .thenThrow(new UserAlreadyExistException("Email is already registered"));
//
//        mockMvc
//                .perform(
//                        post("/register")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(validRequest)))
//                .andExpect(status().isConflict())
//                .andExpect(content().string("Email is already registered"));
//    }
//
//    @Test
//    void testLoginSuccess() throws Exception {
//        when(authService.login(validLoginRequest)).thenReturn(validResponseDto);
//        mockMvc
//                .perform(
//                        post("/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(validLoginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("jwttoken"))
//                .andExpect(jsonPath("$.email").value("john@test.com"))
//                .andExpect(jsonPath("$.role").value("STUDENT"))
//                .andExpect(jsonPath("$.userName").value("John"))
//                .andExpect(jsonPath("$.address").value("123 adress street 4576"));
//    }
//
//    @Test
//    void testLoginFail() throws Exception {
//        mockMvc
//                .perform(
//                        post("/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(invalidLoginRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testChangePasswordSuccess() throws Exception {
//        when(authService.changePassword(validPassRequest)).thenReturn(true);
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(validPassRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Password changed successfully"));
//    }
//
//    @Test
//    void testChangePasswordBlankFields() throws Exception {
//        PasswordRequestDTO dto = new PasswordRequestDTO("", "", "");
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testChangePasswordInvalidPattern() throws Exception {
//        // Missing special character and upper case
//        PasswordRequestDTO dto = new PasswordRequestDTO("Password1", "password", "password");
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testChangePasswordTooShort() throws Exception {
//        PasswordRequestDTO dto = new PasswordRequestDTO("Pwd@1", "Pwd@1", "Pwd@1");
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testChangePasswordInvalidOldPassword() throws Exception {
//        PasswordRequestDTO dto = new PasswordRequestDTO("WrongOld@1", "NewPass@1", "NewPass@1");
//
//        Mockito.doThrow(new InvalidLoginException("Login email or password is wrong"))
//                .when(authService)
//                .changePassword(dto);
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Login email or password is wrong"));
//    }
//
//    @Test
//    void testChangePasswordUserNotFound() throws Exception {
//        PasswordRequestDTO dto = new PasswordRequestDTO("OldPass@1", "NewPass@1", "NewPass@1");
//
//        Mockito.doThrow(new UserNotFoundException("This user is not registered"))
//                .when(authService)
//                .changePassword(any(PasswordRequestDTO.class));
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("This user is not registered"));
//    }
//
//    @Test
//    void testChangePasswordPasswordMismatch() throws Exception {
//        PasswordRequestDTO dto = new PasswordRequestDTO("OldPass@1", "NewPass@1", "Mismatch@1");
//        when(authService.changePassword(dto))
//                .thenThrow(new IllegalArgumentException("New passwords do not match"));
//
//        mockMvc
//                .perform(
//                        post("/api/password")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string("New passwords do not match"));
//    }
// }
