package ru.practicum.ewm.categories.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.service.mapper.Mapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CategoryDtoMapper extends Mapper {

    public static Category mapDtoToCategory(CategoryDto categoryDto) {
        Category category = new Category(
                0L,
                categoryDto.getName()
        );
        log.trace(DEFAULT_MESSAGE, categoryDto, category);
        return category;
    }

    public static CategoryResponseDto mapCategoryToResponseDto(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
                category.getId(),
                category.getName()
        );
        log.trace(DEFAULT_MESSAGE, category, categoryResponseDto);
        return categoryResponseDto;
    }

}
