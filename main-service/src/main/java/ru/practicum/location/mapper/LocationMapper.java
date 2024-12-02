package ru.practicum.location.mapper;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

public class LocationMapper {

    public static Location toLocation(LocationDto dto) {
        return new Location(
                dto.getLat(),
                dto.getLon()
        );
    }

    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
