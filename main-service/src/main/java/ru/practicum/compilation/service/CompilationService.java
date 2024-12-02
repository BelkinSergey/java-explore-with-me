package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto get(Long compId);

    CompilationDto update(UpdateCompilationDto dto, Long compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);
}
