package ru.practicum.ewm.participations.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class ParticipationNotFoundException extends NotFoundException {
    public ParticipationNotFoundException(long participationId) {
        super("Запрос на участие с id = " + participationId + " не найден");
    }
}
