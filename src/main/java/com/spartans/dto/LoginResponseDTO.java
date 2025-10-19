package com.spartans.dto;

public record LoginResponseDTO(String email,
                               String role,
                               String token,
                               String userName,
                               String contactNumber,
                               String address) {
}
