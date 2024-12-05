package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.RequestMapper;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.RequestOutDto;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestOutDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Нет пользователя по id %d", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Нет события по id %d", userId)));
        final Request requestValid = repository.findByRequesterIdAndEventId(userId, eventId);
        if (Objects.nonNull(requestValid)) {
            throw new ConflictException("Пользователь уже подал заявку на участи в событии");
        }
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь не может подать заяку на участие в своем же мероприятии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя подавать заявку на неопубликованное мероприятие");
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0) {
            throw new ConflictException("На данное мероприятие больше нет мест");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 ||
                (!event.getRequestModeration() && event.getParticipantLimit() > event.getConfirmedRequests())) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            Request newRequest = repository.save(request);
            log.info("Заявка на участие сохранена со статусом CONFIRMED");
            return RequestMapper.toRequestOutDto(newRequest);

        }
        if (!event.getRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            request.setStatus(Status.REJECTED);
            final Request newRequest = repository.save(request);
            log.info("Заявка на участие сохранена со статусом REJECTED, так как превышен лимит");
            return RequestMapper.toRequestOutDto(newRequest);
        }
        request.setStatus(Status.PENDING);
        return RequestMapper.toRequestOutDto(repository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestOutDto> getRequestsByEventOwner(Long userId, Long eventId) {
        checkUser(userId);
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException(String.format("Нет события по параметрам userId %d и eventId %d", userId, eventId)));

        return repository.findAllByEventId(eventId).stream().map(RequestMapper::toRequestOutDto).toList();
    }

    @Override
    public Map<String, List<RequestOutDto>> updateRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(event.getInitiator(), user)) {
            throw new ConflictException("Пользователь не является инициатором этого события.");
        }

        final List<Request> requests = repository.findRequestByIdIn(statusUpdateRequest.getRequestIds());

        if (event.getRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0 && statusUpdateRequest.getStatus().equals(Status.CONFIRMED)) {
            throw new ConflictException("Лимит заявок на участие в событии исчерпан.");
        }


        final boolean verified = requests.stream()
                .allMatch(request -> request.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new ConflictException("Список запросов не относятся к одному событию.");
        }

        final Map<String, List<RequestOutDto>> requestMap = new HashMap<>();

        if (statusUpdateRequest.getStatus().equals(Status.REJECTED)) {
            if (requests.stream()
                    .anyMatch(request -> request.getStatus().equals(Status.CONFIRMED))) {
                throw new ConflictException("Запрос на установление статуса <ОТМЕНЕНА>. Подтвержденные заявки нельзя отменить.");
            }
            log.info("Запрос на отклонение заявки подтвержден.");

            List<RequestOutDto> rejectedRequests = requests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .map(repository::save)
                    .map(RequestMapper::toRequestOutDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
        } else {
            if (requests.stream()
                    .anyMatch(request -> !request.getStatus().equals(Status.PENDING))) {
                throw new ConflictException("Запрос на установление статуса <ПОДТВЕРЖДЕНА>. Заявки должны быть со статусом <В ОЖИДАНИИ>.");
            }

            long limit = event.getParticipantLimit() - event.getConfirmedRequests();
            final List<Request> confirmedList = requests.stream()
                    .limit(limit)
                    .peek(request -> request.setStatus(Status.CONFIRMED))
                    .map(repository::save).toList();
            log.info("Заявки на участие сохранены со статусом <ПОДТВЕРЖДЕНА>.");

            final List<RequestOutDto> confirmedRequests = confirmedList.stream()
                    .map(RequestMapper::toRequestOutDto)
                    .toList();
            requestMap.put("confirmedRequests", confirmedRequests);

            final List<Request> rejectedList = requests.stream()
                    .skip(limit)
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .map(repository::save).toList();
            log.info("Часть заявок на участие сохранены со статусом <ОТМЕНЕНА>, в связи с превышением лимита.");
            final List<RequestOutDto> rejectedRequests = rejectedList.stream()
                    .map(RequestMapper::toRequestOutDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
            event.setConfirmedRequests(confirmedList.size() + event.getConfirmedRequests());
            eventRepository.save(event);
        }
        return requestMap;
    }


    @Override
    @Transactional(readOnly = true)
    public List<RequestOutDto> getAllRequests(final Long userId) {
        final List<Request> requests = repository.findByRequesterId(userId);

        if (requests.isEmpty()) {
            log.info("Заявок на участие в мероприятии, у пользователя с id {} пока нет.", userId);
            return new ArrayList<>();
        }
        log.info("Получение списка всех заявок участия пользователя с id {}.", userId);
        return requests.stream().map(RequestMapper::toRequestOutDto).toList();
    }

    @Override
    public RequestOutDto cancelRequest(final Long userId, final Long requestId) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} не существует." + userId));
        final Request request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявки с id = {} не существует." + requestId));

        if (!request.getRequester().equals(user)) {
            throw new ConflictException("Отменить заявку может только пользователь иницировавший её.");
        }

        request.setStatus(Status.CANCELED);
        final Request requestCancel = repository.save(request);
        log.info("Заявка на участие с id = {} отменена.", requestId);

        final Event event = request.getEvent();
        if (event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
            log.info("Появилоась свободное место у события с id = {}.", event.getId());
        }

        return RequestMapper.toRequestOutDto(requestCancel);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Нет такого пользователя по id %d", userId)));
    }
}