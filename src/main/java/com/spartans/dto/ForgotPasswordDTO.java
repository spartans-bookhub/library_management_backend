package com.spartans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        String email
) {
}
