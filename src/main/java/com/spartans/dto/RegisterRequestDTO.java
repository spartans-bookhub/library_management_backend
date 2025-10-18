package com.spartans.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        //login details
        @NotBlank(message = "Login email is required.")
        @Email(message = "Email id must be valid.")
        String email,
        @NotBlank(message = "Password is required.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character"
        )
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        //user details
        @NotBlank(message = "Name is required.")
        String name,
        @Pattern(regexp = "^(?:\\+91|91)?[789]\\d{9}$", message = "Invalid mobile number")
        String contactNumber,
        String address

) {
}
