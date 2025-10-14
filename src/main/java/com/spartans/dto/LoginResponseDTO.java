package com.spartans.dto;

public record LoginResponseDTO(String loginId,
                               String role,
                               String token) {
}
