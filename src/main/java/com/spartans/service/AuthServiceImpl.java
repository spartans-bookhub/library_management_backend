package com.spartans.service;

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
import com.spartans.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthRepository authRepo;

    @Autowired
    UserMapper mapper;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtil;


    @Override
    public boolean register(RegisterRequestDTO request) {
        //Check if user registration already exists
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
            userAuth = authRepo.save(userAuth);
        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistException("Email already registered");
        } catch (Exception ex) {
            throw new DBException("Failed to save user");
        }
        return true;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        UserAuth userAuth = authRepo.findById(request.email())
                .orElseThrow(() -> new UserNotFoundException("Email is not registered"));

        if (userAuth.getRole().equals("STUDENT") && userAuth.getStudent() == null) {
            throw new UserNotFoundException("Student is not found");
        }

        // Validate password
        validatePassword(request.password(), userAuth.getPassword());

        // Generate JWT
        String token = jwtUtil.generateToken(userAuth);
        return mapper.toLoginDto(userAuth.getStudent(), userAuth.getEmail(), userAuth.getRole(), token);
    }

    @Override
    public boolean changePassword(PasswordRequestDto request) {
        UserAuth userAuth = authRepo.findById(UserContext.getEmail())
                .orElseThrow(() -> new UserNotFoundException("This user is not registered"));
        if (validatePassword(request.oldPassword(), userAuth.getPassword())) {
            userAuth.setPassword(passwordEncoder.encode(request.newPassword()));
            authRepo.save(userAuth);
            return true;
        }
        return false;
    }

    private boolean validatePassword(String password, String savedPassword) {
        if (passwordEncoder.matches(password, savedPassword)) {
            return true;
        }
        throw new InvalidLoginException("Login email or password is wrong");
    }

}
