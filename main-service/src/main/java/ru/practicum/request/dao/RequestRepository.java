package ru.practicum.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.Status;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {

    Integer countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByEventId(Long eventId);

    Request findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findRequestByIdIn(List<Integer> requestIds);

    List<Request> findByRequesterId(Long userId);
}
