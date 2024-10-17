package com.eventbook.eventservice.presentation.controllers;

import com.eventbook.eventservice.domain.models.Event;
import com.eventbook.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event, @RequestHeader("User-Id") Long userId) {
        return eventService.createEvent(event, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event, @RequestHeader("User-Id") Long userId) {
        return eventService.updateEvent(id, event, userId);
    }


    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvent(@RequestParam String name) {
        List<Event> events = eventService.findEventbyName(name);
        if (events.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(events);
    }
}