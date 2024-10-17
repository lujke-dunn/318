package com.example.booking.dto;

public class TicketDTO {

    private String seatNumber;
    private double price;

    // Constructors, Getters, and Setters
    public TicketDTO() {}

    public TicketDTO(String seatNumber, double price) {
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

