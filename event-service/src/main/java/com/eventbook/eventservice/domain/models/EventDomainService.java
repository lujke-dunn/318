package com.eventbook.eventservice.domain.models;



import com.eventbook.eventservice.domain.models.Event;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class EventDomainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void validateEvent(Event event) {
        validateName(event.getName());
        validateDate(event.getDate());
        validateLocation(event.getLocation());
        validateDescription(event.getDescription());
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Event name cannot exceed 100 characters");
        }
    }

    private void validateDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Event date cannot be empty");
        }
        try {
            LocalDate eventDate = LocalDate.parse(date, DATE_FORMATTER);
            if (eventDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Event date cannot be in the past");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Event location cannot be empty");
        }
        if (location.length() > 200) {
            throw new IllegalArgumentException("Event location cannot exceed 200 characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("Event description cannot exceed 1000 characters");
        }
    }
    }