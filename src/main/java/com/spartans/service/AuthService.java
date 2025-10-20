package com.spartans.service;

import com.spartans.dto.*;

public interface AuthService {
  public boolean register(RegisterRequestDTO request);

  public LoginResponseDTO login(LoginRequestDTO request);

  public boolean changePassword(PasswordRequestDTO request);
}
