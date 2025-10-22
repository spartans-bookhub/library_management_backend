package com.spartans.service;

import com.spartans.dto.*;
import com.spartans.exception.*;
import com.spartans.mapper.UserMapper;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import com.spartans.util.JWTUtils;
import com.spartans.util.UserContext;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired AuthRepository authRepo;

  @Autowired UserMapper mapper;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired JWTUtils jwtUtil;

  @Autowired NotificationService notificationService;

  @Override
  public boolean register(RegisterRequestDTO request) {
    // Check if user registration already exists
    if (authRepo.existsById(request.email())) {
      throw new UserAlreadyExistException("Email is already registered: " + request.email());
    }
    UserAuth userAuth = mapper.toUserAuthEntity(request);
    userAuth.setPassword(passwordEncoder.encode(request.password()));
    User user = mapper.toUserEntity(request);
    user.setCreatedAt(LocalDateTime.now());
    user.setUserAuth(userAuth);
    userAuth.setStudent(user);
    try {
      authRepo.save(userAuth);
    } catch (DataIntegrityViolationException ex) {
      throw new UserAlreadyExistException("Email already registered");
    } catch (Exception ex) {
      throw new DBException("Failed to save user");
    }
    return true;
  }

  @Override
  public LoginResponseDTO login(LoginRequestDTO request) {
    UserAuth userAuth =
        authRepo
            .findById(request.email())
            .orElseThrow(() -> new UserNotFoundException("Email is not registered"));

    if (userAuth.getRole().equals("STUDENT") && userAuth.getStudent() == null) {
      throw new UserNotFoundException("Student is not found");
    }

    // Validate password
    validatePassword(
        request.password(), userAuth.getPassword(), "Login email or password is wrong");

    // Generate JWT
    String token = jwtUtil.generateToken(userAuth);
    return mapper.toLoginDto(userAuth.getStudent(), userAuth.getEmail(), userAuth.getRole(), token);
  }

  @Override
  public boolean changePassword(PasswordRequestDTO request) {
    // Check if user exists
    UserAuth userAuth =
        authRepo
            .findById(UserContext.getEmail())
            .orElseThrow(() -> new UserNotFoundException("This user is not registered"));

    // Check if passwords match
    if (!request.newPassword().equals(request.confirmNewPassword()))
      throw new IllegalArgumentException("New passwords do not match");

    // Check if old password is correct
    if (validatePassword(
        request.oldPassword(), userAuth.getPassword(), "Current password is incorrect")) {
      userAuth.setPassword(passwordEncoder.encode(request.newPassword()));
      authRepo.save(userAuth);
      return true;
    }
    return false;
  }

  @Override
  public void processForgotPassword(ForgotPasswordDTO request) {
    // Check if user exists
    UserAuth userAuth =
        authRepo
            .findById(request.email())
            .orElseThrow(() -> new UserNotFoundException("This email is not registered"));

    // Create reset token
    String resetToken = UUID.randomUUID().toString();

    // Add reset link
    userAuth.setResetToken(resetToken);
    authRepo.save(userAuth);

    // send reset link
    notificationService.sendPasswordResetReminder(request.email(), resetToken);
  }

  @Override
  public boolean resetPassword(ResetPasswordDTO request) {
    // Check if passwords match
    if (!request.newPassword().equals(request.confirmNewPassword()))
      throw new IllegalArgumentException("New password and confirmation do not match");

    // Validate and fetch userAuth with the token
    UserAuth userAuth =
        authRepo
            .findByResetToken(request.resetToken())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Password cannot be reset with the same URL again."));

    userAuth.setPassword(passwordEncoder.encode(request.newPassword()));
    userAuth.setResetToken("");
    authRepo.save(userAuth);
    return true;
  }

  private boolean validatePassword(String password, String savedPassword, String errorMessage) {
    if (passwordEncoder.matches(password, savedPassword)) {
      return true;
    }
    throw new InvalidLoginException(errorMessage);
  }
}
