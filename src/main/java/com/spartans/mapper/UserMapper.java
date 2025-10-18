package com.spartans.mapper;

import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {
    StudentResponseDTO toUserDto(User user);
    User toUserEntity(RegisterRequestDTO registerRequest);
    UserAuth toUserAuthEntity(RegisterRequestDTO registerRequest);
    LoginResponseDTO toLoginDto(UserAuth userAuth, String token);
}


