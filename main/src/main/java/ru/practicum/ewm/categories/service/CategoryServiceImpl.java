package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.categories.exception.CategoryNotEmptyException;
import ru.practicum.ewm.categories.mapper.CategoryDtoMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.service.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.categories.mapper.CategoryDtoMapper.mapCategoryToResponseDto;
import static ru.practicum.ewm.categories.mapper.CategoryDtoMapper.mapDtoToCategory;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryResponseDto create(CategoryDto categoryDto) {
        Category savedCategory = categoryRepository.saveAndFlush(mapDtoToCategory(categoryDto));
        log.debug("Категория {} сохранена", savedCategory);
        return mapCategoryToResponseDto(savedCategory);
    }

    @Transactional
    @Override
    public void delete(long catId) {
        Category category = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(catId));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryNotEmptyException(catId);
        }
        categoryRepository.delete(category);
        log.debug("Категория с id = {} удалена", catId);
    }

    @Transactional
    @Override
    public CategoryResponseDto update(long catId, CategoryDto categoryDto) {
        Category storedCategory = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(catId));
        if (!storedCategory.getName().equals(categoryDto.getName())) {
            log.debug("Наименование категории {} изменено на {}", storedCategory.getName(), categoryDto.getName());
            storedCategory.setName(categoryDto.getName());
        }
        return mapCategoryToResponseDto(storedCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDto> getAll(int from, int size) {
        List<Category> allCategories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();
        log.debug("Получен список: {}", allCategories);
        return allCategories.stream().map(CategoryDtoMapper::mapCategoryToResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponseDto get(long catId) {
        Category requestedCategory = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(catId));
        log.debug("Получена категория {}", requestedCategory);
        return mapCategoryToResponseDto(requestedCategory);
    }

}
