package com.spartans.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String studentName;
    @Column(unique = true)
    private String studentEmail;
    private LocalDateTime createdAt;
    private String phone;
    private String address;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "auth_login_id", referencedColumnName = "loginId")
    private UserAuth auth;

    // One student has many transactions
    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    public Student(Long studentId, String studentName, String studentEmail, LocalDateTime createdAt, String phone, String address, UserAuth auth, List<Transaction> transactions) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.createdAt = createdAt;
        this.phone = phone;
        this.address = address;
        this.auth = auth;
        this.transactions = transactions;
    }

    public Student(String studentName, String studentEmail, LocalDateTime createdAt) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.createdAt = createdAt;
    }


    public UserAuth getAuth() {
        return auth;
    }

    public void setAuth(UserAuth auth) {
        this.auth = auth;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

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

    public Student(String studentName, String studentEmail, LocalDateTime createdAt, String phone, String address) {
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
