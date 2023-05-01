package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventValidationException extends EwmException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public EventValidationException(String message) {
        super(message);
    }

}
