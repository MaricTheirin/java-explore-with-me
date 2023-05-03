package ru.practicum.ewm.requests.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(long participationId) {
        super("Запрос на участие с id = " + participationId + " не найден");
    }
}
