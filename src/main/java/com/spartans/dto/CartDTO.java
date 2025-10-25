package com.spartans.dto;

public record CartDTO(
    Long id,
    Long bookId,
    String bookTitle,
    String bookAuthor,
    String category,
    String isbn,
    String imageUrl) {}
