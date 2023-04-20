package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;

public interface CategoriesService {

    CategoryResponseDto create(CategoryDto categoryDto);

    void delete(long catId);

    CategoryResponseDto update(long catId, CategoryDto categoryDto);

}
