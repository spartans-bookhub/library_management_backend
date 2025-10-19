package com.spartans.mapper;

import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.userAuth.email", target = "email")
    @Mapping(source = "user.userAuth.role", target = "role")
    StudentResponseDTO toUserDto(User user);

    @Mapping(source = "name", target = "userName")
    User toUserEntity(RegisterRequestDTO registerRequest);

    @Mapping(target = "role", constant = "STUDENT")
    UserAuth toUserAuthEntity(RegisterRequestDTO registerRequest);

    @Mapping(source = "user.userName", target = "userName")
    @Mapping(source = "user.contactNumber", target = "contactNumber")
    @Mapping(source = "user.address", target = "address")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "token", target = "token")
    LoginResponseDTO toLoginDto(User user, String email, String role, String token);
}


