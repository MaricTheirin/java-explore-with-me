package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public abstract class EwmException extends RuntimeException {

    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public EwmException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }

}
