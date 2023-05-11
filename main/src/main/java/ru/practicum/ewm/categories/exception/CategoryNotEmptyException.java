package ru.practicum.ewm.categories.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class CategoryNotEmptyException extends EwmException {

    public CategoryNotEmptyException(long categoryId) {
        super("Категория с id = " + categoryId + " содержит события", HttpStatus.CONFLICT);
    }

}
