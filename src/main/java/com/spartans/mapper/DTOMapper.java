package com.spartans.mapper;

import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.model.UserAuth;
import com.spartans.model.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DTOMapper {
    public UserAuth toAuth(RegisterRequestDTO registerRequest) {
        //TO DO : change STUDENT to enum
        return new UserAuth(registerRequest.loginId(), registerRequest.password(), "STUDENT", toStudent(registerRequest));
    }

    public Student toStudent(RegisterRequestDTO registerRequest) {
        return new Student(registerRequest.name(), registerRequest.email(), LocalDateTime.now(), registerRequest.phone(), registerRequest.address());

    }

    public StudentResponseDTO toStudentDto(Student student) {
        return new StudentResponseDTO(student.getStudentId(), student.getStudentName(), student.getStudentEmail(),student.getPhone(), student.getAddress());


    }

    public LoginResponseDTO toLoginResponseDto(UserAuth userAuth) {
        return new LoginResponseDTO(userAuth.getLoginId(), userAuth.getRole(), userAuth.getStudent());
    }
}
