package ru.practicum.ewm.comments.exception;

import org.springframework.http.HttpStatus;
import ru.practicum.ewm.service.exception.EwmException;

public class NotAvailableToCommentException extends EwmException {
    public NotAvailableToCommentException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
