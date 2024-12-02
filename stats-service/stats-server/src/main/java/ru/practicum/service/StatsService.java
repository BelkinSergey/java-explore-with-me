package ru.practicum.service;


import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.ViewsStatsRequest;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHit hit);

    List<ViewStats> getViewStatsList(ViewsStatsRequest request);
}