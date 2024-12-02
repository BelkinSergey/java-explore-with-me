package ru.practicum.event.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByInitiatorId(Long id, PageRequest pageable);

    List<Event> findAllByCategoryId(Long catId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findByInitiatorInAndStateInAndCategoryInAndEventDateAfterAndEventDateBefore(List<User> users,
                                                                                            List<State> eventStates,
                                                                                            List<Category> categories,
                                                                                            LocalDateTime rangeStart,
                                                                                            LocalDateTime rangeEnd,
                                                                                            PageRequest pageable);

    List<Event> findByIdIn(List<Long> events);

}
