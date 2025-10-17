package com.spartans.mapper;

import com.spartans.dto.UserDTO;
import com.spartans.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDto);
}
