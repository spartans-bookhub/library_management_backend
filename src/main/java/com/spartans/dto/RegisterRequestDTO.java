package com.spartans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
    //login details
    @NotBlank(message = "Login Id is required.")
    String loginId,
    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    //user details
    @NotBlank(message = "Student name is required.")
    String name,
    @Email(message = "Email id must be valid.")
    String email,
    String phone,
    String address
) {
}
