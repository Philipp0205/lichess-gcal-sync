package de.philipp.lichessgcalsync.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.services.calendar.model.Event;

import de.philipp.lichessgcalsync.service.GoogleCalendarService;

@RestController
public class CalendarController {

    private final GoogleCalendarService googleCalendarService;

    public CalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        try {
            return googleCalendarService.getUpcomingEvents();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to fetch events", e);
        }
    }
}
