package com.spartans.service;

import com.spartans.dto.LoginRequestDTO;
import com.spartans.dto.LoginResponseDTO;
import com.spartans.dto.RegisterRequestDTO;
import com.spartans.dto.StudentResponseDTO;

public interface AuthService {
    public StudentResponseDTO register(RegisterRequestDTO request);
    public LoginResponseDTO login(LoginRequestDTO request);
}
