package com.spartans.dto;

public record LoginResponseDTO(
        Long userId,
        String email,
        String role,
        String token,
        String userName,
        String contactNumber,
        String address) {
}
