package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dao.CompilationRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        log.info("Запрос на сохранение подборки администратором");
        List<Event> events = eventRepository.findByIdIn(dto.getEvents());
        Compilation compilation = CompilationMapper.toCompilation(dto, events);
        if (Objects.isNull(dto.getPinned())) {
            compilation.setPinned(false);
        }
        Compilation newCompilation = repository.save(compilation);
        log.info("подборка успешно сохранена администратором");
        return CompilationMapper.toCompilationDto(newCompilation, events.stream()
                .map(EventMapper::toEventShortDto)
                .toList());
    }

    @Override
    public void delete(Long compId) {
        log.info("Запрос на удаление подборки администратором с id = {}", compId);
        repository.deleteById(compId);
        log.info("Подборка с id = {} успешно удалена администратором", compId);
    }

    @Override
    public CompilationDto get(Long compId) {
        log.info("Запрос на получение данных подборки с id = {}", compId);
        final Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = {} нет." + compId));
        log.info("Подборка с id = {} успешно получена", compId);
        return CompilationMapper.toCompilationDto(compilation, compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .toList());
    }

    @Override
    public CompilationDto update(UpdateCompilationDto dto, Long compId) {
        log.info("Запрос на обновление данных подборки с id = {}", compId);
        final Compilation compilation = repository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = {} нет." + compId));
        final List<Event> events = eventRepository.findByIdIn(dto.getEvents());
        if (Objects.nonNull(dto.getEvents())) {
            compilation.setEvents(events);
        }
        if (Objects.nonNull(dto.getPinned())) {
            compilation.setPinned(dto.getPinned());
        }
        if (Objects.nonNull(dto.getTitle())) {
            compilation.setTitle(dto.getTitle());
        }
        final Compilation newCompilation = repository.save(compilation);
        log.info("Успешное обновление данных подборки с id = {}", compId);
        return CompilationMapper.toCompilationDto(newCompilation, events.stream()
                .map(EventMapper::toEventShortDto)
                .toList());
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        log.info("Запрос на получение списка всех подборок");
        List<Compilation> compilationList;
        if (Objects.nonNull(pinned)) {
            compilationList = repository.findByPinned(pinned, pageable);
        } else {
            compilationList = repository.findAll(pageable).toList();
        }
        if (compilationList.isEmpty()) {
            log.info("Еще не создано ни одной подборки");
            return new ArrayList<>();
        }
        log.info("Найден список подборок");
        return compilationList.stream()
                .map(c -> CompilationMapper.toCompilationDto(c, c.getEvents().stream()
                        .map(EventMapper::toEventShortDto).toList())).toList();
    }


}
