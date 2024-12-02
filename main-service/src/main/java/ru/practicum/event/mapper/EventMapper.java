package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventIntoDto;
import ru.practicum.event.dto.EventOutFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.location.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEvent(EventIntoDto eventIntoDto, Category category, User user, Location location) {

        return new Event(
                null,
                eventIntoDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                eventIntoDto.getDescription(),
                eventIntoDto.getEventDate(),
                user,
                location,
                eventIntoDto.getPaid(),
                eventIntoDto.getParticipantLimit(),
                null,
                eventIntoDto.getRequestModeration(),
                State.PENDING,
                eventIntoDto.getTitle(),
                0,
                0L

        );
    }

    public static EventOutFullDto toEventOutFullDto(Event event) {
        return new EventOutFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryOutDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()

        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryOutDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()

        );
    }

}
