package com.gymbrut.app.model;

public class PaymentTransaction {
    private final String title;
    private final String amount;
    private final String status;

    public PaymentTransaction(String title, String amount, String status) {
        this.title = title;
        this.amount = amount;
        this.status = status;
    }

    public String getTitle() { return title; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
}
