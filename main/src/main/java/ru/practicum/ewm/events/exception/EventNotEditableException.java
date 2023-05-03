package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventNotEditableException extends EwmException {

    public EventNotEditableException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
