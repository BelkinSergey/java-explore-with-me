package ru.practicum.event.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventIntoDto;
import ru.practicum.event.dto.EventOutFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.UpdateEventIntoDto;
import ru.practicum.event.model.Marker;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestOutDto;
import ru.practicum.request.service.RequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService service;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventOutFullDto create(@PathVariable("userId") @NotNull Long id,
                                  @RequestBody @Validated(Marker.OnCreate.class) EventIntoDto eventIntoDto) {

        return service.create(id, eventIntoDto);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> get(@PathVariable("userId") @NotNull Long id,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.get(id, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventOutFullDto getEventByOwner(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        return service.getEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventOutFullDto update(@PathVariable @NotNull Long userId,
                                  @PathVariable @NotNull Long eventId,
                                  @RequestBody @Validated(Marker.OnUpdate.class) UpdateEventIntoDto dto) {
        return service.update(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestOutDto> getRequestsByEventOwner(@PathVariable @NotNull Long userId,
                                                       @PathVariable @NotNull Long eventId) {
        return requestService.getRequestsByEventOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, List<RequestOutDto>> updateRequestsStatus(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateRequestsStatus(userId, eventId, request);
    }
}
