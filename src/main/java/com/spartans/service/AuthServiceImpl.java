package com.spartans.service;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.InvalidLoginException;
import com.spartans.exception.UserNotFoundException;
import com.spartans.mapper.DTOMapper;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthRepository authRepo;

    @Autowired
    StudentService studentService;
    @Autowired
    DTOMapper mapper;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public StudentResponseDTO register(RegisterRequestDTO request) {
        //add to auth table
        UserAuth userAuth = mapper.toAuth(request);
        userAuth.setPassword(passwordEncoder.encode(userAuth.getPassword()));
        userAuth = authRepo.save(userAuth);
        return mapper.toStudentDto(userAuth.getStudent());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        UserAuth userAuth = authRepo.findById(request.loginId())
                .orElseThrow(() -> new UserNotFoundException("Invalid login"));
        //Student is not found for this login Id
        //change to enum
        if (userAuth.getRole().equals("STUDENT") && userAuth.getStudent() == null)
            throw new UserNotFoundException("Student is not found");

        //Validate password
        if (!passwordEncoder.matches(request.password(), userAuth.getPassword())) {
            throw new InvalidLoginException("Login Id or password is wrong");
        }

        //auth valid
        return mapper.toLoginResponseDto(userAuth);

    }
}
