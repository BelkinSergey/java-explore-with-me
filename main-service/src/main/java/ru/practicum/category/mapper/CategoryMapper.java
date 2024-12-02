package ru.practicum.category.mapper;

import ru.practicum.category.dto.CategoryIntoDto;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(CategoryIntoDto dto) {
        return new Category(
                null,
                dto.getName()
        );
    }

    public static CategoryOutDto toCategoryOutDto(Category category) {
        return new CategoryOutDto(
                category.getId(),
                category.getName()
        );
    }

}
