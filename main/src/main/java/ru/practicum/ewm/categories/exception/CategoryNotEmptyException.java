package ru.practicum.ewm.categories.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class CategoryNotEmptyException extends EwmException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public CategoryNotEmptyException(long categoryId) {
        super("Категория с id = " + categoryId + " содержит события");
    }

}
