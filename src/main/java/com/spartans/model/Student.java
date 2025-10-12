package com.spartans.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private LocalDateTime createdAt;
    private String phone;
    private String address;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Student() {
    }

    public Student(Long studentId, String studentName, String studentEmail, LocalDateTime createdAt, String phone, String address) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.createdAt = createdAt;
        this.phone = phone;
        this.address = address;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
