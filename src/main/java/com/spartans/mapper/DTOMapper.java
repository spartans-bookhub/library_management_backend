package com.spartans.mapper;

import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DTOMapper {
    public UserAuth toAuth(RegisterRequestDTO registerRequest) {
        return new UserAuth(registerRequest.loginId(), registerRequest.password(), "STUDENT");
    }

    public User toStudent(RegisterRequestDTO registerRequest) {
        return new User(registerRequest.name(), registerRequest.loginId(), LocalDateTime.now());

    }

    public StudentResponseDTO toStudentDto(User student) {
        return new StudentResponseDTO(student.getUserId(), student.getUserName(), student.getUserEmail(), student.getPhone(), student.getAddress());


    }

    public LoginResponseDTO toLoginResponseDto(UserAuth userAuth, String token) {
        return new LoginResponseDTO(userAuth.getLoginId(), userAuth.getRole(), token);
    }


}
