package ru.practicum.request;

import ru.practicum.request.dto.RequestOutDto;
import ru.practicum.request.model.Request;

public class RequestMapper {

    public static RequestOutDto toRequestOutDto(Request request) {
        return new RequestOutDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
