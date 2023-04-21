package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public class NotEmptyException extends EwmException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public NotEmptyException(String message) {
        super(message);
    }

}
