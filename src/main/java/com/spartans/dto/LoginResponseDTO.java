package com.spartans.dto;

import com.spartans.model.Student;

public record LoginResponseDTO(String loginId,
                               String role,
                               Student student) {
}
