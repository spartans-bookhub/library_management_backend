package com.spartans.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookDTO(
    Long bookId,
    String bookTitle,
    String bookAuthor,
    String category,
    String isbn,
    String imageUrl,
    String publisherName,
    LocalDate publicationDate,
    Double price,
    LocalDateTime createdAt,
    Integer totalCopies,
    Integer availableCopies,
    Integer rating) {}
