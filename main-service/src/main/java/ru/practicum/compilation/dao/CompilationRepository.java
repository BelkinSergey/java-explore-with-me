package ru.practicum.compilation.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Set;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Set<Event> findAllByIdIn(List<Long> events);

    List<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}
