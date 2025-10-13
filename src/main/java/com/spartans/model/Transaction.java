package com.spartans.model;

import com.spartans.service.StudentServiceImpl;
import jakarta.persistence.*;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.time.LocalDate;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private Double fineAmount;
    private String penaltyReason;
    private String transactionStatus; // "BORROWED", "RETURNED", "OVERDUE"

    // Many transactions belong to one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "studentId")
    private Student student;

    // Many transactions involve one book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "bookId")
    private Book book;

    public Transaction(){}

    public Transaction(Long transactionId, Book book, Student student, String status, String penaltyReason, Double fineAmount, LocalDate dueDate, LocalDate returnDate, LocalDate borrowDate) {
        this.transactionId = transactionId;
        this.book = book;
        this.student = student;
        this.transactionStatus = status;
        this.penaltyReason = penaltyReason;
        this.fineAmount = fineAmount;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.borrowDate = borrowDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getPenaltyReason() {
        return penaltyReason;
    }

    public void setPenaltyReason(String penaltyReason) {
        this.penaltyReason = penaltyReason;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
