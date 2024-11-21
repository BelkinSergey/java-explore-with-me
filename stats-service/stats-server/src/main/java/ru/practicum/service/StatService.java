package ru.practicum.service;

import ru.practicum.StatDto;
import ru.practicum.StatIntoDto;
import ru.practicum.StatOutDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatDto create(StatIntoDto statIntoDto);

    List<StatOutDto> get(LocalDateTime start,
                         LocalDateTime end,
                         List<String> uris,
                         Boolean unique);
}
