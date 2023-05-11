package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends EwmException {

    public NotFoundException(long id) {
        super("Сущность с id = " + id + " не обнаружена", HttpStatus.NOT_FOUND);
    }

}
