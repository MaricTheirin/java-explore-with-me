package ru.practicum.ewm.categories.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class CategoryAlreadyExistException extends EwmException {
    
    public CategoryAlreadyExistException(String categoryName) {
        super("Категория с наименованием \"" + categoryName + "\" уже существует", HttpStatus.CONFLICT);
    }

}
