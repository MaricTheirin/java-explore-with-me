package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.categories.service.CategoryService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {

    private final CategoryService categoriesService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Запрошено добавление категории {}", categoryDto);
        return categoriesService.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive long catId) {
        log.info("Запрошено удаление категории с id = {}", catId);
        categoriesService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryResponseDto updateCategory(
            @PathVariable @Positive long catId,
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        log.info("Запрошено обновление категории с id = {} следующими данными: {}", catId, categoryDto);
        return categoriesService.update(catId, categoryDto);
    }

}
