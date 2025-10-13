package com.spartans.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Fine {
    @Id
    @GeneratedValue
    private Long fineId;

    @OneToOne
    private Transaction transaction;
    private double amount;
    private boolean paid;

    public Long getId() {
        return fineId;
    }

    public void setId(Long id) {
        this.fineId = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Fine(){}

    public Fine(Long id, Transaction transaction, double amount, boolean paid) {
        this.fineId = id;
        this.transaction = transaction;
        this.amount = amount;
        this.paid = paid;
    }
}
