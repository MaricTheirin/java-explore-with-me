package ru.practicum.ewm.users.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(long id) {
        super("Пользователь с id = " + id + " не обнаружен");
    }
}
