package com.spartans.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordDTO(
    @NotBlank
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message =
                "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
        String newPassword,
    String resetToken) {}
