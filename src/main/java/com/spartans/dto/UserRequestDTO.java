package com.spartans.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    String userName,
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message =
                "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String contactNumber,
    String address,
    String email) {}
