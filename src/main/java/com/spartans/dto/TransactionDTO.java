package com.spartans.dto;

import java.time.LocalDate;

public record TransactionDTO(
    String userName,
    Long bookId,
    String bookTitle,
    LocalDate borrowDate,
    LocalDate returnDate,
    LocalDate dueDate,
    Double fineAmount,
    String transactionStatus, // BORROWED, RETURNED, DUE
    String penaltyReason) {}
