package ru.practicum.request.service;

import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestOutDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    RequestOutDto create(Long userId, Long eventId);

    List<RequestOutDto> getRequestsByEventOwner(Long userId, Long eventId);

    Map<String, List<RequestOutDto>> updateRequestsStatus(Long userId,
                                                          Long eventId,
                                                          EventRequestStatusUpdateRequest request);

    List<RequestOutDto> getAllRequests(Long userId);

    RequestOutDto cancelRequest(Long userId, Long requestId);
}
