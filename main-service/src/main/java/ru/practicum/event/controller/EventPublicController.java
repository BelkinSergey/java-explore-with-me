package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventOutFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.SearchEventParams;
import ru.practicum.event.service.EventService;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@Valid SearchEventParams searchEventParams,
                                            HttpServletRequest request) {
        log.info("GET запрос на получения событий с фильтром");
        return eventService.getAllEventFromPublic(searchEventParams, request);
    }

    @GetMapping("/{eventId}")
    public EventOutFullDto getEventById(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                        HttpServletRequest request) {
        log.info("GET запрос на получения полной информации о событии с  id= {}", eventId);
        return eventService.getEventById(eventId, request);
    }
}