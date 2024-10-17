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
public class PaymentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public PaymentConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"payment-received-topic", "payment-failed-topic"}, groupId = "notification-service-group")
    public void listen(String message) {
        logger.info("Received payment message: {}", message);
        try {
            Map<String, Object> paymentData = objectMapper.readValue(message, Map.class);
            String userId = String.valueOf(paymentData.get("userId"));
            String status = (String) paymentData.get("status");
            double amount = ((Number) paymentData.get("amount")).doubleValue();
            String bookingId = String.valueOf(paymentData.get("bookingId"));

            NotificationType notificationType = "SUCCESS".equals(status) ? 
                NotificationType.PAYMENT_RECEIVED : NotificationType.PAYMENT_FAILED;

            String notificationMessage = "SUCCESS".equals(status) ?
                String.format("Payment of $%.2f has been successfully processed for booking ID %s", amount, bookingId) :
                String.format("Payment of $%.2f has failed for booking ID %s. Please try again.", amount, bookingId);

            notificationService.createNotification(userId, notificationMessage, notificationType);
            
            logger.info("Notification created for payment {} for user ID: {}", status, userId);
        } catch (Exception e) {
            logger.error("Error processing payment message", e);
        }
    }
}
