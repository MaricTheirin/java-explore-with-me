package ru.practicum.ewm.compilations.exception;

import ru.practicum.ewm.service.exception.NotFoundException;

public class CompilationNotFoundException extends NotFoundException {

    public CompilationNotFoundException(long compilationId) {
        super("Подборка событий с id = " + compilationId + " не обнаружена");
    }

}
