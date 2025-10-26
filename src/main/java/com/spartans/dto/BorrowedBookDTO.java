package com.spartans.dto;

import java.time.LocalDate;

public class BorrowedBookDTO {
  private Long transactionId;
  private Long bookId;
  private String bookTitle;
  private Long userId;
  private LocalDate borrowDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private Double fineAmount;
  private String transactionStatus;

  // Constructors
  public BorrowedBookDTO() {}

  public BorrowedBookDTO(
      Long transactionId,
      Long bookId,
      String bookTitle,
      Long userId,
      LocalDate borrowDate,
      LocalDate dueDate,
      LocalDate returnDate,
      Double fineAmount,
      String transactionStatus) {
    this.transactionId = transactionId;
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.userId = userId;
    this.borrowDate = borrowDate;
    this.dueDate = dueDate;
    this.returnDate = returnDate;
    this.fineAmount = fineAmount;
    this.transactionStatus = transactionStatus;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public LocalDate getBorrowDate() {
    return borrowDate;
  }

  public void setBorrowDate(LocalDate borrowDate) {
    this.borrowDate = borrowDate;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDate getReturnDate() {
    return returnDate;
  }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public void setReturnDate(LocalDate returnDate) {
    this.returnDate = returnDate;
  }

  public Double getFineAmount() {
    return fineAmount;
  }

  public void setFineAmount(Double fineAmount) {
    this.fineAmount = fineAmount;
  }
}
