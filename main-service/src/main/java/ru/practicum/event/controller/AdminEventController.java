package ru.practicum.event.controller;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventOutFullDto;
import ru.practicum.event.dto.UpdateEventIntoDto;
import ru.practicum.event.model.Marker;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{event-id}")
    @ResponseStatus(HttpStatus.OK)
    public EventOutFullDto updateEventByAdmin(@PathVariable("event-id") Long eventId,
                                              @RequestBody @Validated(Marker.OnUpdate.class) UpdateEventIntoDto updateEventAdminRequest) {
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventOutFullDto> getEventsByAdminParams(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) final String rangeStart,
            @RequestParam(required = false) final String rangeEnd,
            @RequestParam(value = "from", defaultValue = "0")
            @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Positive Integer size) {

        return eventService.getEventsByAdminParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }

}
