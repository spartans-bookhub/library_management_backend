package com.spartans.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    long userId,
    String userName,
    String contactNumber,
    String address,
    String email) {}
