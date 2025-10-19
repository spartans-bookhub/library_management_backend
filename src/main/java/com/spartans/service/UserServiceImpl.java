package com.spartans.service;

import com.spartans.dto.StudentResponseDTO;
import com.spartans.exception.*;
import com.spartans.mapper.UserMapper;
import com.spartans.model.User;
import com.spartans.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired UserMapper mapper;

  @Autowired UserRepository userRepository;

  @Override
  public StudentResponseDTO getStudent(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with this email"));
    return mapper.toUserDto(user);
  }
}
