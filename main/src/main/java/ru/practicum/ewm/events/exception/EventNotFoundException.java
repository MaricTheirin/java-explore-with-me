package ru.practicum.ewm.events.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class EventNotFoundException extends NotFoundException {

    public EventNotFoundException(long eventId) {
        super("Событие с id = " + eventId + " не обнаружено");
    }

    public EventNotFoundException(long userId, long eventId) {
        super("У пользователья с id = " + userId + " нет события с id = " + eventId);
    }
}
