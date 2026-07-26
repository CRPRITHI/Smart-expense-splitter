package com.expensesplitter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Expense {

    private int id;
    private String description;
    private BigDecimal amount;
    private int paidBy;
    private int groupId;
    private SplitType splitType;

    private List<Split> splits = new ArrayList<>();

    public Expense() {
    }

    public Expense(String description, BigDecimal amount, int paidBy, int groupId) {
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.groupId = groupId;
    }

    public Expense(int id, String description, BigDecimal amount, int paidBy, int groupId) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(int paidBy) {
        this.paidBy = paidBy;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public SplitType getSplitType() {
    return splitType;
}

public void setSplitType(SplitType splitType) {
    this.splitType = splitType;
}

    public List<Split> getSplits() {
        return splits;
    }

    public void setSplits(List<Split> splits) {
        this.splits = splits;
    }

    public void addSplit(Split split) {
        this.splits.add(split);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", paidBy=" + paidBy +
                ", groupId=" + groupId +
                '}';
    }
}