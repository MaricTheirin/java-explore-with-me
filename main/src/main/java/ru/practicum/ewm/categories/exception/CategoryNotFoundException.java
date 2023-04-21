package ru.practicum.ewm.categories.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(long id) {
        super("Категория с id = " + id + " не обнаружена");
    }

}
