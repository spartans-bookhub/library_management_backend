package com.spartans.service;

import com.spartans.dto.RegisterRequestDTO;
import com.spartans.mapper.DTOMapper;
import com.spartans.model.Student;
import com.spartans.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    DTOMapper mapper;
    @Override
    public Student createStudent(RegisterRequestDTO request) {
        return studentRepository.save(mapper.toStudent(request));
    }
}
