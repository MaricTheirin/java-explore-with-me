package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.categories.service.CategoryService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.info("Запрошен список категорий, страница = {}, размер выдачи = {}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryResponseDto get(@PathVariable @Positive long catId) {
        log.info("Запрошена категория по id = {}", catId);
        return categoryService.get(catId);
    }

}
