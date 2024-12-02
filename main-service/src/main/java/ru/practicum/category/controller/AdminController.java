package ru.practicum.category.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryIntoDto;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.model.Marker;


@Slf4j
@RestController
@Validated
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryOutDto create(@RequestBody @Validated(Marker.OnCreate.class) CategoryIntoDto categoryIntoDto) {
        log.info("Получили данные на создание категории {}", categoryIntoDto);
        return categoryService.create(categoryIntoDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull Long catId) {
        log.info("Получили данные на удаление категории по id {}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryOutDto patch(@RequestBody @Validated(Marker.OnCreate.class) CategoryIntoDto categoryIntoDto,
                                @PathVariable("catId") Long id) {
        log.info("Получили данные {} на обновление категории по id {}", categoryIntoDto, id);
        return categoryService.patch(categoryIntoDto, id);
    }
}
