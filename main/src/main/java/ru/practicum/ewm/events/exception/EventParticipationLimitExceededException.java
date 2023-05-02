package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventParticipationLimitExceededException extends EwmException {

    private final HttpStatus status = HttpStatus.CONFLICT;

    public EventParticipationLimitExceededException(long eventId) {
        super("В событии с id = " + eventId + " нет свободных мест");
    }

}
