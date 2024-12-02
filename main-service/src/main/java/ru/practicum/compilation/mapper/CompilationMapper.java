package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(final NewCompilationDto compilationRequestDto,
                                            final List<Event> evens) {

        final Compilation compilation = new Compilation();

        compilation.setEvents(evens);
        compilation.setPinned(compilationRequestDto.getPinned());
        compilation.setTitle(compilationRequestDto.getTitle());

        return compilation;
    }

    public static CompilationDto toCompilationDto(final Compilation compilation,
                                                  final List<EventShortDto> eventShortDto) {

        return new CompilationDto(
                compilation.getId(),
                eventShortDto,
                compilation.getPinned(),
                compilation.getTitle()

        );
    }
}
