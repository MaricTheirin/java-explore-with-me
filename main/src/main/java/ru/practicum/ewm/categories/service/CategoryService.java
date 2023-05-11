package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import java.util.List;

public interface CategoryService {

    CategoryResponseDto create(CategoryDto categoryDto);

    void delete(long catId);

    CategoryResponseDto update(long catId, CategoryDto categoryDto);

    List<CategoryResponseDto> getAll(int from, int size);

    CategoryResponseDto get(long catId);

}
