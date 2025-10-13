package com.spartans.model;

import jakarta.persistence.*;


@Entity
public class BookInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    private int totalCopies;
    private int availableCopies;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "bookId")
    private Book book;

    public BookInventory() {
    }

    public BookInventory(Long inventoryId, int totalCopies, int availableCopies) {
        this.inventoryId = inventoryId;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
