package com.example.booking.dto;

public class PaymentDTO {

    private double amount;
    private String paymentMethod;

    // Constructors, Getters, and Setters
    public PaymentDTO() {}

    public PaymentDTO(double amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

