package com.spartans.service;

import com.spartans.dto.StudentResponseDTO;

public interface UserService {

  StudentResponseDTO getStudent(Long id);
}
