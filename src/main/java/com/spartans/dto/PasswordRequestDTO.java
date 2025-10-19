package com.spartans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordRequestDTO(
        @NotBlank
        String oldPassword,
        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
        )
        String newPassword
) {
}
