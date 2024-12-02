package ru.practicum.event.service;


import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;

import java.util.List;

public interface EventService {
    EventOutFullDto create(Long id, EventIntoDto eventIntoDto);

    List<EventShortDto> get(Long id, Integer from, Integer size);

    EventOutFullDto getEventByUser(Long userId, Long eventId);

    EventOutFullDto update(Long userId, Long eventId, UpdateEventIntoDto dto);

    EventOutFullDto updateEventByAdmin(Long eventId, UpdateEventIntoDto updateEventAdminRequest);

    List<EventOutFullDto> getEventsByAdminParams(List<Long> users, List<String> states, List<Long> categories,
                                                 String rangeStart, String rangeEnd, Integer from, Integer size);

    List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request);

    EventOutFullDto getEventById(Long eventId, HttpServletRequest request);
}
