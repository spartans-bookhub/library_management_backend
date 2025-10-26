package com.spartans.model;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private Integer quantity = 1;

    public CartItem() {}

    public CartItem(Book book) {
        this.book = book;
        this.quantity = 1;
    }


    public Long getId() { return id; }
    public Book getBook() { return book; }
    public Integer getQuantity() { return quantity; }
    public void setId(Long id) { this.id = id; }
    public void setBook(Book book) { this.book = book; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
