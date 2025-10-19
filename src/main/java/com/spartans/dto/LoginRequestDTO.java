package com.spartans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Login email cannot be blank") @Email(message = "Email id must be valid.")
        String email,
    @NotBlank(message = "Password cannot be blank") String password) {}
