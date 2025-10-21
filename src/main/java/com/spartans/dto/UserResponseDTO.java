package com.spartans.dto;

public record UserResponseDTO(
    String userName, String email, String contactNumber, String address, String role) {}
