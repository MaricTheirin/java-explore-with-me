package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends EwmException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
