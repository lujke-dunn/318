package com.example.booking.repository;

import com.example.booking.domain.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final BookingRepository bookingRepository;

    @Autowired
    public DataLoader(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Add some data on startup to ensure the database is created
       // Booking booking = new Booking();
        //booking.setEvent("Concert");
        //booking.setNumberOfTickets(2);
        //bookingRepository.save(booking);
    }
}
