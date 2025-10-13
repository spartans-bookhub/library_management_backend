package com.spartans.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String category;
    private String isbn;
    private String imageUrl;
    private String publisherName;
    private LocalDate publicationDate;


    @OneToOne(mappedBy = "book")
    private BookInventory inventory;

    public Book() {
    }

    public Book(String bookTitle, String bookAuthor, String category, String isbn, String imageUrl, String publisherName, LocalDate publicationDate, BookInventory inventory) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.category = category;
        this.isbn = isbn;
        this.imageUrl = imageUrl;
        this.publisherName = publisherName;
        this.publicationDate = publicationDate;
        this.inventory = inventory;
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

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public BookInventory getInventory() {
        return inventory;
    }

    public void setInventory(BookInventory inventory) {
        this.inventory = inventory;
    }
}
