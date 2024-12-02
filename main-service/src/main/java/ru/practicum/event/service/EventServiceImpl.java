package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHit;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateActionAdmin;
import ru.practicum.event.model.StateActionPrivate;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.dao.LocationRepository;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.request.dao.RequestRepository;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constants.DateTime.DATE_TIME_PATTERN;
import static ru.practicum.event.model.State.PENDING;
import static ru.practicum.event.model.State.PUBLISHED;
import static ru.practicum.event.model.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.event.model.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.event.model.StateActionPrivate.CANCEL_REVIEW;
import static ru.practicum.event.model.StateActionPrivate.SEND_TO_REVIEW;
import static ru.practicum.request.model.Status.CONFIRMED;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventRepository repository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Value("${stats-server.application.name:service}")
    private String applicationName;

    static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public EventOutFullDto create(Long id, EventIntoDto eventIntoDto) {

        checkTime(eventIntoDto.getEventDate());
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователя нет в базе по id %d", id)));


        Category category = getCategoryById(eventIntoDto.getCategory());

        Location location = checkLocation(LocationMapper.toLocation(eventIntoDto.getLocation()));

        Event event = EventMapper.toEvent(eventIntoDto, category, user, location);

        return EventMapper.toEventOutFullDto(repository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> get(Long id, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        List<Event> events = repository.findByInitiatorId(id, pageable);

        if (events == null || events.isEmpty()) {
            return new ArrayList<>();
        } else {
            return events.stream().map(EventMapper::toEventShortDto).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventOutFullDto getEventByUser(Long userId, Long eventId) {
        final Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} не существует." + eventId));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ValidationException("Пользователь не является инициатором этого события.");
        }

        log.info("Получение события с id = {}", eventId);
        return EventMapper.toEventOutFullDto(event);

    }

    @Override
    public EventOutFullDto update(Long userId, Long eventId, UpdateEventIntoDto dto) {

        Event event = repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Нет события c id %d ", eventId)));

        if (event.getState().equals(PUBLISHED)) {
            throw new ForbiddenException("Нельзя изменять опубликованные события");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime eventTime = getEventDateFormat(dto.getEventDate());
            checkTime(eventTime);
            event.setEventDate(eventTime);
        }
        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory((getCategoryById(dto.getCategory())));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        if (dto.getLocation() != null) {
            Location location = checkLocation(LocationMapper.toLocation(dto.getLocation()));
            event.setLocation(location);
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (dto.getStateAction() != null) {
            StateActionPrivate stateActionPrivate = StateActionPrivate.valueOf(dto.getStateAction());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }
        }
        event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        return EventMapper.toEventOutFullDto(event);
    }

    @Override
    public EventOutFullDto updateEventByAdmin(Long eventId, UpdateEventIntoDto updateEvent) {

        Event event = getEventById(eventId);

        if (event.getViews() == null) {
            event.setViews(0L);
        }

        if (updateEvent.getStateAction() != null) {
            StateActionAdmin stateAction = StateActionAdmin.valueOf(updateEvent.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ForbiddenException("Событие не может быть опубликовано, т.к. статус не PENDING");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ForbiddenException("Нельзя отклонить опубликованное событие.");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }
        String annotation = updateEvent.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }

        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEvent.getCategory()).orElseThrow(
                    () -> new NotFoundException("")));
        }
        String description = updateEvent.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        if (updateEvent.getEventDate() != null) {
            LocalDateTime eventTime = getEventDateFormat(updateEvent.getEventDate());
            checkTime(eventTime);
            event.setEventDate(eventTime);
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(checkLocation(LocationMapper.toLocation(updateEvent.getLocation())));
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        String title = updateEvent.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }

        event.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));

        return EventMapper.toEventOutFullDto(repository.save(event));
    }

    @Override
    public List<EventOutFullDto> getEventsByAdminParams(List<Long> usersId, List<String> states,
                                                        List<Long> categoriesId, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)) : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)) : LocalDateTime.now().plusYears(20);
        PageRequest pageable = PageRequest.of(from / size, size);
        log.info("Заявка на получение событий по переданным параметрам");
        if (start.isAfter(end)) {
            throw new ValidationException("Временной промежуток задан неверно");
        }
        final List<User> users;
        if (Objects.isNull(usersId) || usersId.isEmpty()) {
            users = userRepository.findAll();
            if (users.isEmpty()) {
                log.info("Еще нет ни одного пользователя, а значит и событий нет");
                return new ArrayList<>();
            }
        } else {
            users = userRepository.findByIdIn(usersId, pageable);
            if (users.size() != usersId.size()) {
                throw new ValidationException("Список пользователей передан неверно");
            }
        }
        final List<State> eventStates;
        if (Objects.isNull(states) || states.isEmpty()) {
            eventStates = List.of(State.PUBLISHED, State.CANCELED, State.PENDING);
        } else {
            try {
                eventStates = states.stream()
                        .map(State::valueOf)
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Недопустимое значение статуса: " + e.getMessage());
            }
        }
        List<Category> categories;
        if (categoriesId == null) {
            categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                log.info("Еще нет ни одной категории, а значит и событий нет");
                return new ArrayList<>();
            }
        } else {
            categories = categoryRepository.findByIdIn(categoriesId, pageable);
            if (categories.size() != categoriesId.size()) {
                throw new ValidationException("Список категорий передан неверно неверно");
            }
        }
        final List<Event> events = repository
                .findByInitiatorInAndStateInAndCategoryInAndEventDateAfterAndEventDateBefore(
                        users, eventStates, categories, start, end, pageable);
        if (events.isEmpty()) {
            log.info("По данным параметрам не нашлось ни одного события");
            return new ArrayList<>();
        }
        log.info("Получен список событий по заданным параметрам");
        return events.stream().map(EventMapper::toEventOutFullDto).toList();
    }


    @Override
    public List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request) {

        if (searchEventParams.getRangeEnd() != null && searchEventParams.getRangeStart() != null) {
            if (searchEventParams.getRangeEnd().isBefore(searchEventParams.getRangeStart())) {
                throw new ValidationException("Дата окончания не может быть раньше даты начала");
            }
        }

        addStatsClient(request);

        Pageable pageable = PageRequest.of(searchEventParams.getFrom() / searchEventParams.getSize(), searchEventParams.getSize());

        Specification<Event> specification = Specification.where(null);
        LocalDateTime now = LocalDateTime.now();

        if (searchEventParams.getText() != null) {
            String searchText = searchEventParams.getText().toLowerCase();
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + searchText + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchText + "%")
                    ));
        }

        if (searchEventParams.getCategories() != null && !searchEventParams.getCategories().isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(searchEventParams.getCategories()));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(searchEventParams.getRangeStart(), now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (searchEventParams.getRangeEnd() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), searchEventParams.getRangeEnd()));
        }

        if (searchEventParams.getOnlyAvailable() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), State.PUBLISHED));

        List<Event> resultEvents = repository.findAll(specification, pageable).getContent();
        List<EventShortDto> result = resultEvents
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        Map<Long, Long> viewStatsMap = getViewsAllEvents(resultEvents);


        for (EventShortDto event : result) {
            Long viewsFromMap = viewStatsMap.getOrDefault(event.getId(), 0L);
            event.setViews(viewsFromMap);


        }

        return result;
    }


    @Override
    public EventOutFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = repository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found by id: " + eventId));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
        }

        addStatsClient(request);
        EventOutFullDto eventFullDto = EventMapper.toEventOutFullDto(event);
        Map<Long, Long> viewStatsMap = getViewsAllEvents(List.of(event));
        Long views = viewStatsMap.getOrDefault(event.getId(), 0L);
        eventFullDto.setViews(views);
        return eventFullDto;
    }


    private void addStatsClient(HttpServletRequest request) {
        statsClient.postStats(EndpointHit.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private Map<Long, Long> getViewsAllEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedOn)
                .toList();
        LocalDateTime earliestDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> viewStatsMap = new HashMap<>();

        if (earliestDate != null) {
            ResponseEntity<Object> response = statsClient.getStats(earliestDate, LocalDateTime.now(),
                    uris, true);

            List<ViewStats> viewStatsList = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            viewStatsMap = viewStatsList.stream()
                    .filter(statsDto -> statsDto.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(
                            statsDto -> Long.parseLong(statsDto.getUri().substring("/events/".length())),
                            ViewStats::getHits));
        }
        return viewStatsMap;
    }

    private LocalDateTime getEventDateFormat(String dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(dateTime, formatter);
        } catch (DateTimeParseException e) {

            throw new ValidationException("Неправильный формат времени");
        }
    }

    private Event getEventById(Long eventId) {

        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Нет события c id %d", eventId)));
    }

    private Event getEvent(Long eventId, Long userId) {
        return repository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Нет события c id %d у пользователя по id %d ", userId, eventId)));

    }

    private void checkTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Incorrectly made request.");
        }
    }

    private Location checkLocation(Location location) {
        if (locationRepository.existsByLatAndLon(location.getLat(), location.getLon())) {
            return locationRepository.findByLatAndLon(location.getLat(), location.getLon());
        } else {
            return locationRepository.save(location);
        }
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Нет события c id %d ", id)));
    }
}

