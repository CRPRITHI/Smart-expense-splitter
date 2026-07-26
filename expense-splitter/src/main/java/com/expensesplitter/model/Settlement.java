package com.expensesplitter.model;

public class Settlement {

    private int id;
    private int payerId;
    private int receiverId;
    private double amount;

    public Settlement() {
    }
    public Settlement( int payerId, int receiverId, double amount) {
    
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public Settlement(int id, int payerId, int receiverId, double amount) {
        this.id = id;
        this.payerId = payerId;
        this.receiverId = receiverId;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
