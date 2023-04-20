package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends RuntimeException {

    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
