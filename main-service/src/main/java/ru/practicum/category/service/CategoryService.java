package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryIntoDto;
import ru.practicum.category.dto.CategoryOutDto;

import java.util.List;

public interface CategoryService {

    CategoryOutDto create(CategoryIntoDto categoryIntoDto);

    void delete(Long catId);


    List<CategoryOutDto> get(Integer from, Integer size);

    CategoryOutDto getById(Long catId);

    CategoryOutDto patch(CategoryIntoDto categoryIntoDto, Long id);
}
