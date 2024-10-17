package com.eventbook.eventservice.service;



import com.eventbook.eventservice.domain.models.Event;
import com.eventbook.eventservice.domain.models.EventDomainService;
import com.eventbook.eventservice.infrastructure.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventDomainService eventDomainService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_SERVICE_URL = "http://localhost:8080/users/";
    private static final String EVENT_CREATED_TOPIC = "event-created-topic";
    private static final String EVENT_UPDATED_TOPIC = "event-updated-topic";

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public ResponseEntity<?> createEvent(Event event, Long userId) {
        // Fetch user data from User Service
        Map<String, Object> userData = getUserData(userId);

        if (userData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if the user is an admin
        boolean isAdmin = (boolean) userData.getOrDefault("admin", false);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can create events");
        }

        eventDomainService.validateEvent(event);
        Event savedEvent = eventRepository.save(event);

        // Publish event created message
        String message = String.format("New event created: %s (ID: %d)", savedEvent.getName(), savedEvent.getId());
        kafkaTemplate.send(EVENT_CREATED_TOPIC, message);

        return ResponseEntity.ok(savedEvent);
    }

    private Map<String, Object> getUserData(Long userId) {
        String url = USER_SERVICE_URL + userId;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching user data: " + e.getMessage());
            return null;
        }
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findEventbyName(String name) {
        return eventRepository.findByNameContaining(name);
    }

    public ResponseEntity<?> updateEvent(Long id, Event updatedEvent, Long userId) {
        Map<String, Object> userData = getUserData(userId);

        if (userData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if the user is an admin
        boolean isAdmin = (boolean) userData.getOrDefault("admin", false);
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only administrators can edit events");
        }

        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            event.setName(updatedEvent.getName());
            event.setDate(updatedEvent.getDate());
            event.setLocation(updatedEvent.getLocation());
            event.setDescription(updatedEvent.getDescription());

            eventDomainService.validateEvent(event);
            Event savedEvent = eventRepository.save(event);
            try {
                // Publish event updated message
                String message = String.format("Event updated: %s (ID: %d)", savedEvent.getName(), savedEvent.getId());
                kafkaTemplate.send(EVENT_UPDATED_TOPIC, message);
            } catch (Exception e) {
                logger.error("Error sending Kafka message for updated event", e);
            }

            return ResponseEntity.ok(savedEvent);
        }
        return ResponseEntity.notFound().build();
    }
}
