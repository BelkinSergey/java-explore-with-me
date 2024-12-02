package ru.practicum.request.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestOutDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestOutDto create(@PathVariable @NotNull Long userId, @RequestParam @NotNull Long eventId) {
        return service.create(userId, eventId);
    }

    @GetMapping
    public List<RequestOutDto> getAllRequests(@PathVariable @Positive final Long userId) {
        return service.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestOutDto cancelRequest(@PathVariable @Positive final Long userId, @PathVariable @Positive final Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}