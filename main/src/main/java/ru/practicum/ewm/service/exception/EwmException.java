package ru.practicum.ewm.service.exception;

import org.springframework.http.HttpStatus;

public abstract class EwmException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public EwmException(String message) {
        super(message);
    }

    public EwmException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
