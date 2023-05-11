package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventNotAvailableForParticipationException extends EwmException {

    public EventNotAvailableForParticipationException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

}
