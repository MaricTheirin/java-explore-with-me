package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventNotAvailableForParticipationException extends EwmException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public EventNotAvailableForParticipationException(String message) {
        super(message);
    }

}
