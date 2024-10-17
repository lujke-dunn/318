package com.eventbook.notificationservice.infrastructure.kafka;

import com.eventbook.notificationservice.domain.NotificationType;
import com.eventbook.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Component
public class BookingConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BookingConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public BookingConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booking-confirmed-topic", groupId = "notification-service-group")
    public void listen(String message) {
        logger.info("Received booking message: {}", message);
        try {
            Map<String, Object> bookingData = objectMapper.readValue(message, Map.class);
            Long bookingId = Long.valueOf(bookingData.get("bookingId").toString());
            String userId = String.valueOf(bookingData.get("userId"));
            String notificationMessage = "Booking Confirmed!";
        

            notificationService.createNotification(userId, notificationMessage, NotificationType.BOOKING_CONFIRMED);
            
            logger.info("Notification created for booking confirmation. Booking ID: {}, User ID: {}", bookingId, userId);
        } catch (Exception e) {
            logger.error("Error processing booking message", e);
        }
    }
}
