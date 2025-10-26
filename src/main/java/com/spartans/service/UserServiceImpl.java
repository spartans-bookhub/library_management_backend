package com.spartans.service;

import com.spartans.dto.UserRequestDTO;
import com.spartans.dto.UserResponseDTO;
import com.spartans.exception.*;
import com.spartans.mapper.UserMapper;
import com.spartans.model.User;
import com.spartans.model.UserAuth;
import com.spartans.repository.AuthRepository;
import com.spartans.repository.UserRepository;
import com.spartans.util.UserContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired UserMapper mapper;

  @Autowired UserRepository userRepository;

  @Autowired AuthRepository authRepository;

  @Override
  public UserResponseDTO getUser(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with this email"));
    return mapper.toUserDto(user);
  }

  @Override
  public UserResponseDTO editUser(UserRequestDTO request) {
    // Check if the user exists
    User user =
        userRepository
            .findById(request.userId())
            .orElseThrow(
                () -> new UserNotFoundException("Incorrect user. This user doesn't exist"));
    user.setUserName(request.userName());
    user.setAddress(request.address());
    user.setContactNumber(request.contactNumber());

    // check if corresponding login exists
    UserAuth userAuth =
        authRepository
            .findById(UserContext.getEmail())
            .orElseThrow(
                () -> new UserNotFoundException("Incorrect user. This user doesn't exist"));
    user.setUserAuth(userAuth);
    return mapper.toUserDto(userRepository.save(user));
  }

  @Override
  public List<UserResponseDTO> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream().map((user) -> mapper.toUserDto(user)).collect(Collectors.toList());
  }

  @Override
  public boolean deleteUser(Long id) {
    if (!userRepository.existsById(id)) throw new UserNotFoundException("This user is not found");
    userRepository.deleteById(id);
    return true;
  }
}
