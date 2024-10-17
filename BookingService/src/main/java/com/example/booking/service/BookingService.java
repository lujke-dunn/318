package com.example.booking.service;



import com.example.booking.dto.PaymentDTO;
import com.example.booking.domain.Booking;
import com.example.booking.domain.Payment;
import com.example.booking.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.booking.domain.Ticket;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8080/users/";
    private static final String EVENT_SERVICE_URL = "http://localhost:8081/events/";
    private static final String BOOKING_CONFIRMED_TOPIC = "booking-confirmed-topic";

    public BookingService(BookingRepository bookingRepository, RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.restTemplate = restTemplate;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking createBooking(Booking booking) {
        if (!isUserValid(booking.getUserId())) {
            throw new RuntimeException("Invalid user id");
        }
        if (!isEventValid(booking.getEvent())) {
            throw new RuntimeException("Invalid event id");
        }
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Send message to Kafka for notification
        String message = String.format("{\"bookingId\":%d,\"userId\":%d,\"event\":\"%s\",\"numberOfTickets\":%d,\"totalPrice\":%.2f}",
            savedBooking.getId(), savedBooking.getUserId(), savedBooking.getEvent(), 
            savedBooking.getNumberOfTickets(), calculateTotalPrice(savedBooking));
        kafkaTemplate.send(BOOKING_CONFIRMED_TOPIC, message);
        
        return savedBooking;
    }

    public ResponseEntity<?> deleteBooking(Long bookingId, Long userId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

        if (bookingOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOptional.get();

        // Check if the user is the owner of the booking or an admin
        Map<String, Object> userData = getUserData(userId);

        if (userData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        boolean isAdmin = (boolean) userData.getOrDefault("admin", false);

        if (!booking.getUserId().equals(userId) && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete this booking");
        }

        // If we've gotten this far, the user has permission to delete the booking
        bookingRepository.deleteById(bookingId);

        return ResponseEntity.ok().body("Booking deleted successfully");
    }

    private Map<String, Object> getUserData(Long userId) {
        String url = USER_SERVICE_URL + userId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


    private boolean isUserValid(Long id) {
        String url = USER_SERVICE_URL + id;
        try {
            restTemplate.getForObject(url, String.class);
            System.out.println("User is valid");
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private boolean isEventValid(String eventName) {
        String encodedEventName;
        try {
            encodedEventName = URLEncoder.encode(eventName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding event name: " + e.getMessage());
            return false;
        }

        String url = EVENT_SERVICE_URL + "search?name=" + encodedEventName;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking event validity: " + e.getMessage());
            return false;
        }
    }

    public Booking processPayment(Long id, PaymentDTO paymentDTO) {
        Booking booking = getBooking(id);
        final String paymentServiceUrl = "http://localhost:8081/payment/process";
        Payment paymentResponse = restTemplate.postForObject(paymentServiceUrl, paymentDTO, Payment.class);
        booking.setPayment(paymentResponse);
        Booking savedBooking = bookingRepository.save(booking);

        String status = (paymentDTO.getAmount() > 0) ? "SUCCESS" : "FAILED";
        String message = String.format("{\"bookingId\":%d,\"userId\":%d,\"amount\":%.2f,\"status\":\"%s\"}",
            savedBooking.getId(), savedBooking.getUserId(), paymentDTO.getAmount(), status);

        if ("SUCCESS".equals(status)) {
            kafkaTemplate.send(BOOKING_CONFIRMED_TOPIC, message);
            kafkaTemplate.send("payment-received-topic", message);
        } else {
            kafkaTemplate.send("payment-failed-topic", message);
        }

        return savedBooking;
    }

    private double calculateTotalPrice(Booking booking) {
        return booking.getTickets().stream()
            .mapToDouble(Ticket::getPrice)
            .sum();
    }


}



