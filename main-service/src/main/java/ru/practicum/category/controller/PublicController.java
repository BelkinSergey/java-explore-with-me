package ru.practicum.category.controller;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryOutDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@Validated
@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
public class PublicController {

    private final CategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryOutDto> get(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.get(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryOutDto getById(@PathVariable @NotNull Long catId) {

        return service.getById(catId);
    }

}
