package com.spartans.dto;

import java.time.LocalDate;

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
    Integer totalCopies,
    Integer availableCopies,
    Integer rating) {}
