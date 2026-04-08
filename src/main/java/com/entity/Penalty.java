package com.entity;

public class Penalty {
    private String id;
    private String orderId;
    private int daysLate;
    private double amount;

    public Penalty() {
    }

    public Penalty(String id, String orderId, int daysLate, double amount) {
        this.id = id;
        this.orderId = orderId;
        this.daysLate = daysLate;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String o) {
        this.orderId = o;
    }

    public int getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(int d) {
        this.daysLate = d;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double a) {
        this.amount = a;
    }
}