package com.example.booking.controller;



import com.example.booking.dto.BookingDTO;
import com.example.booking.dto.PaymentDTO;
import com.example.booking.domain.Booking;
import com.example.booking.dto.TicketDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.booking.domain.Ticket;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;
import java.util.stream.Collectors;
import com.example.booking.service.BookingService; // Correctly import the service layer


@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService; // Inject the service layer

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Get all bookings
    @GetMapping
    public List<BookingDTO> getAllBookings() {
        return bookingService.getAllBookings()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get a specific booking by ID
    @GetMapping("/{id}")
    public BookingDTO getBooking(@PathVariable Long id) {
        return convertToDTO(bookingService.getBooking(id));
    }

    // Create a new booking
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        try {
            Booking booking = convertToEntity(bookingDTO);
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.ok(convertToDTO(bookingService.createBooking(createdBooking)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id, @RequestHeader("User-Id") Long userId) {
        return bookingService.deleteBooking(id, userId);
    }

    // Process payment for a booking
    @PostMapping("/{id}/payment")
    public BookingDTO processPayment(@PathVariable Long id, @RequestBody PaymentDTO paymentDTO) {
        return convertToDTO(bookingService.processPayment(id, paymentDTO));
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(booking.getUserId());
        bookingDTO.setEvent(booking.getEvent());
        bookingDTO.setNumberOfTickets(booking.getNumberOfTickets());
        bookingDTO.setTickets(
                booking.getTickets()
                        .stream()
                        .map(ticket -> new TicketDTO(ticket.getSeatNumber(), ticket.getPrice()))
                        .collect(Collectors.toList())
        );
        return bookingDTO;
    }

    private Booking convertToEntity(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setUserId(bookingDTO.getUserId());
        booking.setEvent(bookingDTO.getEvent());
        booking.setNumberOfTickets(bookingDTO.getNumberOfTickets());
        booking.setTickets(
                bookingDTO.getTickets()
                        .stream()
                        .map(ticketDTO -> new Ticket(ticketDTO.getSeatNumber(), ticketDTO.getPrice()))
                        .collect(Collectors.toList())
        );
        return booking;
    }
}

