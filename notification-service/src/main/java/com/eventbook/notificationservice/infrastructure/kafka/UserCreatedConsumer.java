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
public class UserCreatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserCreatedConsumer.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public UserCreatedConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-created-topic", groupId = "notification-service-group")
    public void listen(String message) {
        logger.info("Received user created message: {}", message);
    }
}
