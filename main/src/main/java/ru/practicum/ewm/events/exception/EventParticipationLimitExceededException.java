package ru.practicum.ewm.events.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class EventParticipationLimitExceededException extends EwmException {

    public EventParticipationLimitExceededException(long eventId) {
        super("В событии с id = " + eventId + " нет свободных мест", HttpStatus.CONFLICT);
    }

}
