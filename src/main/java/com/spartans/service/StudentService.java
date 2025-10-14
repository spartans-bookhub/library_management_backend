package com.spartans.service;

import com.spartans.dto.RegisterRequestDTO;
import com.spartans.model.Student;

public interface StudentService {

    public Student createStudent(RegisterRequestDTO request);

    public Student getStudent(Long id);
}
