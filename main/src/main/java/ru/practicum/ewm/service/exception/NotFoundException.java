package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends EwmException {

    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

}
