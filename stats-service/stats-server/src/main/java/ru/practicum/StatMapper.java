package ru.practicum;

import ru.practicum.model.Statistic;

public class StatMapper {

    public static Statistic toStat(StatIntoDto dto) {
        return new Statistic(
                null,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }

    public static StatDto toStatDto(Statistic stat) {
        return new StatDto(
                stat.getId(),
                stat.getApp(),
                stat.getUri(),
                stat.getIp(),
                stat.getTimestamp()
        );
    }

}
