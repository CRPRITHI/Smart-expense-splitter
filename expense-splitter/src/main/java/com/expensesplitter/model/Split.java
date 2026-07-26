package com.expensesplitter.model;

import java.math.BigDecimal;

public class Split {

    private int id;
    private int expenseId;
    private int userId;
    private BigDecimal shareAmount;

    public Split() {
    }

    // Used before expense is saved
    public Split(int userId, BigDecimal shareAmount) {
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    // Used when expense already exists
    public Split(int expenseId, int userId, BigDecimal shareAmount) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    public Split(int id, int expenseId, int userId, BigDecimal shareAmount) {
        this.id = id;
        this.expenseId = expenseId;
        this.userId = userId;
        this.shareAmount = shareAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }

    // Compatibility methods
    public BigDecimal getAmount() {
        return shareAmount;
    }

    public void setAmount(BigDecimal amount) {
        this.shareAmount = amount;
    }

    @Override
    public String toString() {
        return "Split{" +
                "id=" + id +
                ", expenseId=" + expenseId +
                ", userId=" + userId +
                ", shareAmount=" + shareAmount +
                '}';
    }
}