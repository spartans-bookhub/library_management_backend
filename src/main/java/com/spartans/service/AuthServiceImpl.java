package com.spartans.service;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.InvalidLoginException;
import com.spartans.exception.UserAlreadyExistException;
import com.spartans.exception.UserNotFoundException;
import com.spartans.mapper.DTOMapper;
import com.spartans.model.Student;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import com.spartans.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthRepository authRepo;

    @Autowired
    DTOMapper mapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtil;


    @Override
    public StudentResponseDTO register(RegisterRequestDTO request) {
        //Check if student registration already exists
        if (authRepo.existsById(request.loginId())) {
            throw new UserAlreadyExistException("Email is already registered: " + request.loginId());
        }
        UserAuth userAuth = mapper.toAuth(request);
        userAuth.setPassword(passwordEncoder.encode(request.password()));
        Student student = mapper.toStudent(request);
        student.setAuth(userAuth);
        userAuth.setStudent(student);
        userAuth = authRepo.save(userAuth);
        return mapper.toStudentDto(userAuth.getStudent());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        UserAuth userAuth = authRepo.findById(request.loginId())
                .orElseThrow(() -> new UserNotFoundException("Invalid login"));

        //Student is not found for this login Id
        if (userAuth.getRole().equals("STUDENT") && userAuth.getStudent() == null)
            throw new UserNotFoundException("Student is not found");

        //Validate password
        if (!passwordEncoder.matches(request.password(), userAuth.getPassword())) {
            throw new InvalidLoginException("Login Id or password is wrong");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(userAuth.getLoginId(), userAuth.getRole(), userAuth.getStudent());

        return mapper.toLoginResponseDto(userAuth, token);

    }


}
