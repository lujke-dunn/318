package com.example.booking.repository;


import com.example.booking.domain.Booking;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


public interface BookingRepository extends JpaRepository<Booking, Long> {
}

