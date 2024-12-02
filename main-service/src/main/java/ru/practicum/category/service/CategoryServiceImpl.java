package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryIntoDto;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryOutDto create(CategoryIntoDto categoryIntoDto) {
        Category category = CategoryMapper.toCategory(categoryIntoDto);
        category.setName(categoryIntoDto.getName());
        return CategoryMapper.toCategoryOutDto(repository.save(category));
    }

    @Transactional
    @Override
    public void delete(Long catId) {
        if (!repository.existsById(catId)) {
            throw new NotFoundException(String.format("Нет такой категории по id %d", catId));
        }
        List<Event> events = eventRepository.findAllByCategoryId(catId);
        if (!events.isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя удалить категорию, с которой связаны события");
        }
        repository.deleteById(catId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryOutDto> get(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable).map(CategoryMapper::toCategoryOutDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryOutDto getById(Long catId) {

        return CategoryMapper.toCategoryOutDto(repository.findById(catId).orElseThrow(() ->
                new NotFoundException(String.format("Нет такой категории по id %d", catId))));
    }

    @Override
    public CategoryOutDto patch(CategoryIntoDto categoryIntoDto, Long id) {

        final Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id = {} не существует.", id)));

        if (Objects.nonNull(categoryIntoDto.getName())) {
            category.setName(categoryIntoDto.getName());
        }

        final Category categoryUpdate = repository.save(category);
        log.info("Категория с id = {} обновлена.", categoryUpdate.getId());
        return CategoryMapper.toCategoryOutDto(categoryUpdate);
    }
}
