package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatDto;
import ru.practicum.StatIntoDto;
import ru.practicum.StatOutDto;
import ru.practicum.group.Marker;
import ru.practicum.service.StatServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class StatController {

   private final StatServiceImpl statService;

    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto create(@RequestBody @Validated(Marker.OnCreate.class) StatIntoDto statDto) {
        return statService.create(statDto);

    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatOutDto> get(
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        return statService.get(start, end, uris, unique);
    }
}
