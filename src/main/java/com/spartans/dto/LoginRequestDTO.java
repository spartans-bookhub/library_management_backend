package com.spartans.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Login ID cannot be blank") String loginId,
    @NotBlank(message = "Password cannot be blank") String password) {}
