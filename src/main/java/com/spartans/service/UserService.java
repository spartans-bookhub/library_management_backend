package com.spartans.service;

import com.spartans.dto.UserRequestDTO;
import com.spartans.dto.UserResponseDTO;
import java.util.List;

public interface UserService {

  UserResponseDTO getUser(Long id);

  UserResponseDTO editUser(UserRequestDTO request);

  List<UserResponseDTO> getAllUsers();

  boolean deleteUser(Long id);
}
